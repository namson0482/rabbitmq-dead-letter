package com.javainuse.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javainuse.common.CommonUtil;
import com.javainuse.config.properties.AppProperties;
import com.javainuse.model.Employee;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
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

	private final ObjectMapper objectMapper;
	
	@Autowired
    public RabbitMQConsumer(RabbitTemplate rabbitTemplate, AppProperties appProperties, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.appProperties = appProperties;
		this.objectMapper = objectMapper;
	}
	
	private void putIntoParkingLot(Message failedMessage) {
		
        log.info("Retries exeeded putting into parking lot");
        try {
			String str = new String(failedMessage.getBody());
			this.rabbitTemplate.convertAndSend(appProperties.getRabbitmq().getExchange(),
					appProperties.getRabbitmq().getErrorVdcQueue(), str);
		} catch (Exception e) {
		} 
    }

	private boolean hasExceededRetryCount(Message in) {
		List<Map<String, ?>> xDeathHeader = in.getMessageProperties().getXDeathHeader();
		if (xDeathHeader != null && xDeathHeader.size() >= 1) {
			Long count = (Long) xDeathHeader.get(0).get("count");
			log.info("Count: " + count);
			return count >= appProperties.getRabbitmq().getRetry();
		}
		return false;
	}

	@RabbitListener(queues = "${app.rabbitmq.create-vdc-queue}")
	public void receivedMessage(String str, Channel channel, Message in) throws Exception {

		if (hasExceededRetryCount(in)) {
			putIntoParkingLot(in);
			return;
		}
		log.info("Received Message From RabbitMQ: " + str);
		try {
			if(CommonUtil.isException(objectMapper, str)) {
				throw new Exception("Exception retry");
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
}