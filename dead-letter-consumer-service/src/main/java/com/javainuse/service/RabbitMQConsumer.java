package com.javainuse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javainuse.config.properties.AppProperties;
import com.javainuse.model.Employee;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class RabbitMQConsumer {

	private final RabbitTemplate rabbitTemplate;

	private final AppProperties appProperties;
	
	@Autowired
    public RabbitMQConsumer(RabbitTemplate rabbitTemplate, AppProperties appProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.appProperties = appProperties;
    }
	
	private void putIntoParkingLot(Message failedMessage) {
		
        log.info("Retries exeeded putting into parking lot");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
			Employee employee = objectMapper.readValue(new String(failedMessage.getBody()), Employee.class);
			log.info("Recieved Message From RabbitMQ: " + employee);
			this.rabbitTemplate.convertAndSend(appProperties.getRabbitmq().getExchange(),
					appProperties.getRabbitmq().getErrorVdcQueue(), employee);
		} catch (Exception e) {
		} 
//        this.rabbitTemplate.send("VIRTUALCARD_CREATE_VDC_ERROR", failedMessage);
    }

	private boolean hasExceededRetryCount(Message in) {
		List<Map<String, ?>> xDeathHeader = in.getMessageProperties().getXDeathHeader();
		if (xDeathHeader != null && xDeathHeader.size() >= 1) {
			Long count = (Long) xDeathHeader.get(0).get("count");
			log.info("Count: " + count);
			return count >= 3;
		}

		return false;
	}

	@RabbitListener(queues = "${app.rabbitmq.create-vdc-queue}")
	public void recievedMessage(String str, Channel channel, Message in) throws Exception {

		if (hasExceededRetryCount(in)) {
			putIntoParkingLot(in);
			return;
		}
		channel.basicAck(in.getMessageProperties().getDeliveryTag(), false);
		log.info("Recieved Message From RabbitMQ: " + new String(in.getBody()));
	}
}