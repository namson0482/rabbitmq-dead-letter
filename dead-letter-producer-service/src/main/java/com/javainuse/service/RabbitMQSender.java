package com.javainuse.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.javainuse.config.properties.AppProperties;
import com.javainuse.model.CardServiceRequestDTO;
import com.javainuse.model.Employee;

@Service
public class RabbitMQSender {

	private static final Logger logger = LoggerFactory.getLogger(RabbitMQSender.class);
	
	
	private final AppProperties appProperties;
	
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
    public RabbitMQSender(RabbitTemplate rabbitTemplate, AppProperties appProperties) {
		
        this.rabbitTemplate = rabbitTemplate;
        this.appProperties = appProperties;
    }

	public void send(Employee employee) {
		
		rabbitTemplate.convertAndSend(appProperties.getRabbitmq().getExchange(), appProperties.getRabbitmq().getCreateVdcQueue(), employee);
		logger.info("Send msg = " + employee);
		
	}
	
	public void send(CardServiceRequestDTO cardServiceRequestDTO) {
		
		rabbitTemplate.convertAndSend(appProperties.getRabbitmq().getExchange(), appProperties.getRabbitmq().getCreateVdcQueue(), cardServiceRequestDTO);
		logger.info("Send msg = " + cardServiceRequestDTO);
		
	}

}