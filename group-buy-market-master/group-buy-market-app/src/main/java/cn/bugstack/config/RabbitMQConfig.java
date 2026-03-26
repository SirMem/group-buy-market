package cn.bugstack.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.config.producer.exchange}")
    private String exchangeName;

    @Value("${spring.rabbitmq.config.producer.delay_order_cancel.delay_queue}")
    private String delayQueueName;

    @Value("${spring.rabbitmq.config.producer.delay_order_cancel.dlx_exchange}")
    private String dlxExchangeName;

    @Value("${spring.rabbitmq.config.producer.delay_order_cancel.dlx_routing_key}")
    private String dlxRoutingKey;

    @Value("${spring.rabbitmq.config.producer.delay_order_cancel.dlx_process_queue}")
    private String processQueueName;

    @Value("${spring.rabbitmq.config.producer.delay_order_cancel.routing_key}")
    private String routingKey;


    /**
     * 专属交换机
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(exchangeName, true, false);
    }


    @Bean
    public TopicExchange dlxTopicExchange() {
        return new TopicExchange(dlxExchangeName, true, false);
    }
    /**
     * 绑定队列到交换机
     */
    @Bean
    public Binding topicTeamSuccessBinding(
            @Value("${spring.rabbitmq.config.producer.topic_team_success.routing_key}") String routingKey,
            @Value("${spring.rabbitmq.config.producer.topic_team_success.queue}") String queue) {
        return BindingBuilder.bind(new Queue(queue, true))
                .to(topicExchange())
                .with(routingKey);
    }


    @Bean
    public Queue delayOrderCancelQueue() {
        return QueueBuilder.durable(delayQueueName)
                .withArgument("x-dead-letter-exchange", dlxExchangeName)
                .withArgument("x-dead-letter-routing-key", dlxRoutingKey)
                .build();
    }

    @Bean
    public Queue processQueue() {
        return QueueBuilder.durable(processQueueName).build();
    }

    @Bean
    public Binding bindingDelayQueue() {
        return BindingBuilder.bind(delayOrderCancelQueue()).to(new TopicExchange(exchangeName)).with(routingKey);
    }

    @Bean
    public Binding bindingProcessQueue() {
        return BindingBuilder.bind(processQueue()).to(new TopicExchange(dlxExchangeName)).with(dlxRoutingKey);
    }

}
