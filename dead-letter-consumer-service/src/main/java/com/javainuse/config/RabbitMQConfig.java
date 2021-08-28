package com.javainuse.config;

import com.javainuse.config.properties.AppProperties;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class RabbitMQConfig {

    private AppProperties appProperties;

    @Bean
    DirectExchange exchange() {
        return ExchangeBuilder.directExchange(appProperties.getRabbitmq().getExchange()).build();
    }

    @Bean
    Queue createVdcQueue() {
        return QueueBuilder.durable(appProperties.getRabbitmq().getCreateVdcQueue())
                .deadLetterExchange(appProperties.getRabbitmq().getExchange())
                .deadLetterRoutingKey(appProperties.getRabbitmq().getCreateVdcQueueRetry()).build();
    }

    @Bean
    Queue createVdcQueueRetry() {
        return QueueBuilder.durable(appProperties.getRabbitmq().getCreateVdcQueueRetry())
                .deadLetterExchange(appProperties.getRabbitmq().getExchange())
                .deadLetterRoutingKey(appProperties.getRabbitmq().getCreateVdcQueue())
                .ttl(appProperties.getRabbitmq().getTimeToLive()).build();
    }

    @Bean
    Queue errorVdcQueue() {
        return QueueBuilder.durable(appProperties.getRabbitmq().getErrorVdcQueue())
                .deadLetterExchange(appProperties.getRabbitmq().getExchange())
                .deadLetterRoutingKey(appProperties.getRabbitmq().getErrorVdcQueueRetry())
                .build();
    }

    @Bean
    Queue errorVdcQueueRetry() {
        return QueueBuilder.durable(appProperties.getRabbitmq().getErrorVdcQueueRetry())
                .deadLetterExchange(appProperties.getRabbitmq().getExchange())
                .deadLetterRoutingKey(appProperties.getRabbitmq().getErrorVdcQueue())
                .ttl(appProperties.getRabbitmq().getTimeToLive()).build();
    }

    @Bean
    Binding createVdcQueueBinding(Queue createVdcQueue, DirectExchange exchange) {
        return BindingBuilder.bind(createVdcQueue).to(exchange).withQueueName();
    }

    @Bean
    Binding createVdcQueueRetryBinding(Queue createVdcQueueRetry, DirectExchange exchange) {
        return BindingBuilder.bind(createVdcQueueRetry).to(exchange).withQueueName();
    }

    @Bean
    Binding errorVdcQueueBinding(Queue errorVdcQueue, DirectExchange exchange) {
        return BindingBuilder.bind(errorVdcQueue).to(exchange).withQueueName();
    }

    @Bean
    Binding errorVdcQueueRetryBinding(Queue errorVdcQueueRetry, DirectExchange exchange) {
        return BindingBuilder.bind(errorVdcQueueRetry).to(exchange).withQueueName();
    }

//    @Bean
//    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setConcurrentConsumers(2);
//        factory.setMaxConcurrentConsumers(3);
//        factory.setConnectionFactory(connectionFactory);
//        return factory;
//    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
