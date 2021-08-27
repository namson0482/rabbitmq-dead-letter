package com.javainuse.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.javainuse.config.properties.AppProperties;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class RabbitMQConfig {
	
	private AppProperties appProperties;

//	@Bean
//	DirectExchange deadLetterExchange() {
//		return new DirectExchange("deadLetterExchange");
//	}
//
//	@Bean
//	Queue dlq() {
//		return QueueBuilder.durable("deadLetterQueue").build();
//	}
//
//	@Bean
//	Queue queue() {
//		return QueueBuilder.durable("javainuse.queue").withArgument("x-dead-letter-exchange", "deadLetterExchange")
//				.withArgument("x-dead-letter-routing-key", "deadLetter").build();
//	}
//
//	@Bean
//	DirectExchange exchange() {
//		return new DirectExchange("javainuse-direct-exchange");
//	}
//
//	@Bean
//	Binding DLQbinding(Queue dlq, DirectExchange deadLetterExchange) {
//		return BindingBuilder.bind(dlq).to(deadLetterExchange).with("deadLetter");
//	}
//
//	@Bean
//	Binding binding(Queue queue, DirectExchange exchange) {
//		return BindingBuilder.bind(queue).to(exchange).with("javainuse");
//	}
	
	
	
	
// Option 2	
//	@Bean
//    DirectExchange exchange() {
//        return ExchangeBuilder.directExchange(appProperties.getRabbitmq().getExchange()).build();
//    }
//	
//	@Bean
//	Queue createVdcQueue() {
//		return QueueBuilder.durable(appProperties.getRabbitmq().getCreateVdcQueue())
//				.deadLetterExchange(appProperties.getRabbitmq().getExchange())
//				.deadLetterRoutingKey(appProperties.getRabbitmq().getCreateVdcQueueError()).build();
//	}
//	
//	@Bean
//    Queue createVdcQueueError() {
//        return QueueBuilder.durable(appProperties.getRabbitmq().getCreateVdcQueueError())
//                .deadLetterExchange(appProperties.getRabbitmq().getExchange())
//                .build();
//    }
//	
//	@Bean
//    Binding createVdcQueueBinding(Queue createVdcQueue, DirectExchange exchange) {
//        return BindingBuilder.bind(createVdcQueue).to(exchange).withQueueName();
//    }
//
//    @Bean
//    Binding createVdcQueueErrorBinding(Queue createVdcQueueError, DirectExchange exchange) {
//        return BindingBuilder.bind(createVdcQueueError).to(exchange).withQueueName();
//    }
	
	
	
	
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
				.deadLetterRoutingKey(appProperties.getRabbitmq().getCreateVdcQueue()).ttl(10000)
	            .build();
	}
	
	@Bean
    Queue createVdcQueueError() {
        return new Queue(appProperties.getRabbitmq().getCreateVdcQueueError());
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
    Binding createVdcQueueErrorBinding(Queue createVdcQueueError, DirectExchange exchange) {
        return BindingBuilder.bind(createVdcQueueError).to(exchange).withQueueName();
    }
	

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
