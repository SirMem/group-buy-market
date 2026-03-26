package cn.bugstack.domain.trade.service.lock.filter;

import cn.bugstack.domain.trade.adapter.repository.ITradeRepository;

import cn.bugstack.domain.trade.model.entity.GroupBuyActivityEntity;
import cn.bugstack.domain.trade.model.entity.InventoryReservationEntity;
import cn.bugstack.domain.trade.model.entity.TradeLockRuleCommandEntity;
import cn.bugstack.domain.trade.model.entity.TradeLockRuleFilterBackEntity;
import cn.bugstack.domain.trade.service.lock.factory.TradeLockRuleFilterFactory;

import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import cn.bugstack.wrench.design.framework.link.model2.handler.ILogicHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Slf4j
@Service
public class TotalStockOccupyRuleFilter implements ILogicHandler<TradeLockRuleCommandEntity, TradeLockRuleFilterFactory.DynamicContext, TradeLockRuleFilterBackEntity> {

    @Resource
    private ITradeRepository repository;

    @Override
    public TradeLockRuleFilterBackEntity apply(TradeLockRuleCommandEntity requestParameter, TradeLockRuleFilterFactory.DynamicContext dynamicContext) throws Exception {
        log.info("交易规则过滤-总库存剩余数量校验 userId:{} goodsId:{}", requestParameter.getUserId(), requestParameter.getGoodsId());

        String goodsId = requestParameter.getGoodsId();
        String outTradeNo = requestParameter.getOutTradeNo();

        if (StringUtils.isBlank(goodsId) || StringUtils.isBlank(outTradeNo)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER);
        }

        GroupBuyActivityEntity groupBuyActivity = dynamicContext.getGroupBuyActivity();

        if (groupBuyActivity == null) {
            log.error("总库存校验,缺少活动上下文信息");
            throw new AppException(ResponseCode.E0101);
        }

        int ttlMinutes = groupBuyActivity.getValidTime() != null ? groupBuyActivity.getValidTime() : 15;

        InventoryReservationEntity inventoryReservationEntity = new InventoryReservationEntity();
        inventoryReservationEntity.setUserId(requestParameter.getUserId());
        inventoryReservationEntity.setGoodsId(goodsId);
        inventoryReservationEntity.setOutTradeNo(outTradeNo);
        inventoryReservationEntity.setActivityId(groupBuyActivity.getActivityId());
        inventoryReservationEntity.setTeamId(requestParameter.getTeamId());
        inventoryReservationEntity.setStatus(0);
        inventoryReservationEntity.setExpireAt(LocalDateTime.now().plusMinutes(ttlMinutes));

        String reservationId = null;
        try {
            log.info("总库存Redis库存设置 goodsId:{}", goodsId);
            repository.setTotalStock(goodsId);

            log.info("交易规则过滤-总库存预占 userId:{} goodsId:{} orderId:{}",
                    requestParameter.getUserId(), goodsId, outTradeNo);

            reservationId = repository.reserveTotalStockTry(inventoryReservationEntity, ttlMinutes);

            if (reservationId == null) {
                log.warn("总库存不足，预占失败 goodsId:{} orderId:{}", goodsId, outTradeNo);
                throw new AppException(ResponseCode.E0008); // 库存不足
            }

            // 记录预占结果，供后续节点/回滚使用
            dynamicContext.setTotalStockReservationId(reservationId);

            return next(requestParameter, dynamicContext);
        } catch (Exception e) {
            if (reservationId != null) {
                log.info("总库存预占失败，开始回滚 goodsId:{} orderId:{}", goodsId, outTradeNo);
                repository.cancelTotalStock(outTradeNo, goodsId);
                dynamicContext.setTotalStockReservationId(null);
            }
            throw e;
        }
    }
}
