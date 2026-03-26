package cn.bugstack.domain.trade.service.lock;

import cn.bugstack.domain.trade.adapter.repository.ITradeRepository;
import cn.bugstack.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import cn.bugstack.domain.trade.model.entity.*;
import cn.bugstack.domain.trade.model.valobj.GroupBuyProgressVO;
import cn.bugstack.domain.trade.service.ITradeLockOrderService;
import cn.bugstack.domain.trade.service.lock.factory.TradeLockRuleFilterFactory;
import cn.bugstack.wrench.design.framework.link.model2.chain.BusinessLinkedList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author needyou
 * @description 交易订单服务
 * @create 2025-01-11 08:07
 */
@Slf4j
@Service
public class TradeLockOrderService implements ITradeLockOrderService {

    @Resource
    private ITradeRepository repository;
    @Resource
    private BusinessLinkedList<TradeLockRuleCommandEntity, TradeLockRuleFilterFactory.DynamicContext, TradeLockRuleFilterBackEntity> tradeRuleFilter;

    @Override
    public MarketPayOrderEntity queryNoPayMarketPayOrderByOutTradeNo(String userId, String outTradeNo) {
        log.info("拼团交易-查询未支付营销订单:{} outTradeNo:{}", userId, outTradeNo);
        return repository.queryMarketPayOrderEntityByOutTradeNo(userId, outTradeNo);
    }

    @Override
    public GroupBuyProgressVO queryGroupBuyProgress(String teamId) {
        log.info("拼团交易-查询拼单进度:{}", teamId);
        return repository.queryGroupBuyProgress(teamId);
    }

    @Override
    public MarketPayOrderEntity lockMarketPayOrder(UserEntity userEntity, PayActivityEntity payActivityEntity, PayDiscountEntity payDiscountEntity) throws Exception {
        log.info("拼团交易-锁定营销优惠支付订单:{} activityId:{} goodsId:{}", userEntity.getUserId(), payActivityEntity.getActivityId(), payDiscountEntity.getGoodsId());

        TradeLockRuleFilterFactory.DynamicContext dynamicContext = new TradeLockRuleFilterFactory.DynamicContext();
        TradeLockRuleFilterBackEntity tradeLockRuleFilterBackEntity;
        try {
            // 交易规则过滤
            tradeLockRuleFilterBackEntity = tradeRuleFilter.apply(TradeLockRuleCommandEntity.builder()
                            .activityId(payActivityEntity.getActivityId())
                            .userId(userEntity.getUserId())
                            .teamId(payActivityEntity.getTeamId())
                            .goodsId(payDiscountEntity.getGoodsId())
                            .outTradeNo(payDiscountEntity.getOutTradeNo())
                            .build(),
                    dynamicContext);
        } catch (Exception e) {
            // 如果在过滤链中已经预占了总库存，需要及时释放
            if (dynamicContext.getTotalStockReservationId() != null) {
                repository.cancelTotalStock(payDiscountEntity.getOutTradeNo(), payDiscountEntity.getGoodsId());
            }
            throw e;
        }

        // 已参与拼团量 - 用于构建数据库唯一索引使用，确保用户只能在一个活动上参与固定的次数
        Integer userTakeOrderCount = tradeLockRuleFilterBackEntity.getUserTakeOrderCount();

        // 构建聚合对象
        GroupBuyOrderAggregate groupBuyOrderAggregate = GroupBuyOrderAggregate.builder()
                .userEntity(userEntity)
                .payActivityEntity(payActivityEntity)
                .payDiscountEntity(payDiscountEntity)
                .userTakeOrderCount(userTakeOrderCount)
                .build();

        try {
            // 锁定聚合订单 - 这会用户只是下单还没有支付。后续会有2个流程；支付成功、超时未支付（回退）
            return repository.lockMarketPayOrder(groupBuyOrderAggregate);
        } catch (Exception e) {
            // 记录失败恢复量
            repository.recoveryTeamStock(tradeLockRuleFilterBackEntity.getRecoveryTeamStockKey(), payActivityEntity.getValidTime());
            // 如果总库存预占成功过，这里做 Cancel（方法名示意）
            if (tradeLockRuleFilterBackEntity.getTotalStockReservationId() != null) {
                repository.cancelTotalStock(payDiscountEntity.getOutTradeNo(), payDiscountEntity.getGoodsId());
            }

            throw e;
        }

    }

}
