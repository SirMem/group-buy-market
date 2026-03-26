package cn.bugstack.trigger.listener;

import cn.bugstack.domain.trade.adapter.repository.ITradeRepository;
import cn.bugstack.domain.trade.model.entity.InventoryReservationEntity;
import cn.bugstack.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Slf4j
@Component
public class TotalStockTopicListener {

    @Resource
    private ITradeRepository repository;

    /**
     * 1. 预占库存监听 (Reverse)
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "reverseQueue"),
            exchange = @Exchange(value = "${spring.rabbitmq.config.producer.tradeDomain.reservedExchangeName}"),
            key = "${spring.rabbitmq.config.producer.tradeDomain.reservedRoutingKey}"
    ))
    @Transactional(rollbackFor = Exception.class)
    public void reverseListener(InventoryReservationEntity inventoryReservationEntity) {
        log.info("预占商品库存goodsId={}", inventoryReservationEntity.getGoodsId());
        try {
            repository.updateTotalStockReserveCount(inventoryReservationEntity);
        } catch (DuplicateKeyException e) {
            log.warn("重复消费，直接确认 success");
        } catch (AppException e) {
            log.error("业务异常", e);
            throw e; // 建议抛出异常触发重试或死信，除非你确定要吞掉异常
        } catch (Exception e) {
            log.error("未知系统错误", e);
            throw e;
        }
    }

    /**
     * 2. 确认购买监听 (Confirm)
     * 修复点：补充了缺失的 Exchange 和 RoutingKey 配置
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "confirmQueue"),
            exchange = @Exchange(value = "${spring.rabbitmq.config.producer.tradeDomain.confirmExchangeName}"), // 假设你的yml配置是这个名字
            key = "${spring.rabbitmq.config.producer.tradeDomain.confirmRoutingKey}"   // 假设你的yml配置是这个名字
    ))
    @Transactional(rollbackFor = Exception.class)
    public void confirmListener(InventoryReservationEntity inventoryReservationEntity) {
        log.info("确认商品购买，扣减库存goodsId={}", inventoryReservationEntity.getGoodsId());
        try {
            repository.confirmSkuTotalStockDB(inventoryReservationEntity);
        } catch (DuplicateKeyException e) {
            log.warn("已经确认购买 success");
        } catch (AppException e) {
            log.error("业务异常", e);
        }
    }

    /**
     * 3. 取消购买监听 (Cancel)
     * 修复点：修正了 cannel -> cancel 的拼写错误，修正了变量无法解析的问题
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "cancelQueue"),
            exchange = @Exchange(value = "${spring.rabbitmq.config.producer.tradeDomain.cancelExchangeName}"),
            key = "${spring.rabbitmq.config.producer.tradeDomain.cancelRoutingKey}"
    ))
    @Transactional(rollbackFor = Exception.class)
    public void cancelListener(InventoryReservationEntity inventoryReservationEntity) {
        log.info("取消商品购买(超时), 还原库存goodsId={}", inventoryReservationEntity.getGoodsId());
        try {
            repository.cancelTotalStockDB(inventoryReservationEntity);
        } catch (DuplicateKeyException e) {
            log.warn("已取消商品购买 success");
        } catch (AppException e) {
            log.error("业务异常", e);
        }
    }

    /**
     * 4. 退款监听 (Refund)
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "refundQueue"),
            exchange = @Exchange(value = "${spring.rabbitmq.config.producer.tradeDomain.refundExchangeName}"),
            key = "${spring.rabbitmq.config.producer.tradeDomain.refundRoutingKey}"
    ))
    @Transactional(rollbackFor = Exception.class)
    public void refundListener(InventoryReservationEntity inventoryReservationEntity) {
        log.info("退款商品, 还原库存goodsId={}", inventoryReservationEntity.getGoodsId());
        try {
            repository.refundTotalStockDB(inventoryReservationEntity);
        } catch (DuplicateKeyException e) {
            log.warn("已退款商品 success");
        } catch (AppException e) {
            log.error("业务异常", e);
        }
    }
}