package cn.bugstack.infrastructure.utils.event;



import cn.bugstack.domain.trade.model.entity.InventoryReservationEntity;
import cn.bugstack.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author needyou
 * @description 消息发送
 * @create 2024-03-30 12:40
 */
@Slf4j
@Component
public class EventPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Value("${spring.rabbitmq.config.producer.exchange}")
    private String exchangeName;

    @Value("${spring.rabbitmq.config.producer.delay_order_cancel.routing_key}")
    private String delayOrderRoutingKey;

    @Value("${spring.rabbitmq.config.producer.tradeDomain.reservedExchangeName}")
    private String reverseExchangeName;

    @Value("${spring.rabbitmq.config.producer.tradeDomain.reservedRoutingKey}")
    private String reverseRoutingKey;

    @Value("${spring.rabbitmq.config.producer.tradeDomain.confirmExchangeName}")
    private String confirmExchangeName;

    @Value("${spring.rabbitmq.config.producer.tradeDomain.confirmRoutingKey}")
    private String confirmRoutingKey;

    @Value("${spring.rabbitmq.config.producer.tradeDomain.refundExchangeName}")
    private String refundExchangeName;

    @Value("${spring.rabbitmq.config.producer.tradeDomain.refundRoutingKey}")
    private String refundRoutingKey;

    @Value("${spring.rabbitmq.config.producer.tradeDomain.cancelExchangeName}")
    public String cannelExchangeName;

    @Value("${spring.rabbitmq.config.producer.tradeDomain.cancelRoutingKey}")
    public String cannelRoutingKey;

    public void publish(String routingKey, String message) {
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, message, m -> {
                // 持久化消息配置
                m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return m;
            });
        } catch (Exception e) {
            log.error("发送MQ消息失败 team_success message:{}", message, e);
            throw e;
        }
    }

    public void delayOrderPublish(String message, long delayTime) {
        try {
            rabbitTemplate.convertAndSend(exchangeName, delayOrderRoutingKey, message, m -> {
                m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                m.getMessageProperties().setExpiration(String.valueOf(delayTime));
                return m;
            });
        } catch (Exception e) {
            log.error("发送RabbitMQ延迟队列失败message${}", message, e);
            throw e;
        }
    }

    public void reversePublish(InventoryReservationEntity inventoryReservationEntity) {
        try {
            rabbitTemplate.convertAndSend(reverseExchangeName, reverseRoutingKey, inventoryReservationEntity, m -> {
                m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return m;
            });
        } catch (Exception e) {
            log.error("预占库存，发送RabbitMQ信息失败, message:{},", inventoryReservationEntity, e);
            throw e;
        }
    }

    public void confirmPublish(InventoryReservationEntity inventoryReservationEntity) {

        try {
            rabbitTemplate.convertAndSend(confirmExchangeName, confirmRoutingKey, inventoryReservationEntity, m -> {
                m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return m;
            });
        } catch (Exception e) {
            log.error("预占库存，发送RabbitMQ信息失败, message:{},", inventoryReservationEntity, e);
            throw e;
        }
    }


    public void refundPublish(InventoryReservationEntity inventoryReservationEntity) {
        try {
            rabbitTemplate.convertAndSend(refundExchangeName, refundRoutingKey, inventoryReservationEntity, m -> {
                m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return m;
            });
        } catch (Exception e) {
            log.error("预占库存，发送RabbitMQ信息失败, message:{},", inventoryReservationEntity, e);
            throw e;
        }
    }

    public void cancelPublish(InventoryReservationEntity inventoryReservationEntity) {
        try {
            rabbitTemplate.convertAndSend(cannelExchangeName, cannelRoutingKey, inventoryReservationEntity, m -> {
                m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return m;
            });
        } catch (AppException e) {
            log.error("取消总库存, 发送RabbitMQ信息失败，message:{},", inventoryReservationEntity, e);
            throw e;
        }
    }


}
