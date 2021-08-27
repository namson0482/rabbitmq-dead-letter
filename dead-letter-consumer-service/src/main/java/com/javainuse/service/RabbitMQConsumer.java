package com.javainuse.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javainuse.exception.InvalidSalaryException;
import com.javainuse.model.Employee;

@Component
public class RabbitMQConsumer {

	private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);
	
	private RabbitTemplate rabbitTemplate;
	
	@Autowired
    public RabbitMQConsumer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
	
	private void putIntoParkingLot(Message failedMessage) {
		
        logger.info("Retries exeeded putting into parking lot");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
			Employee employee = objectMapper.readValue(new String(failedMessage.getBody()), Employee.class);
			logger.info("Recieved Message From RabbitMQ: " + employee);
			this.rabbitTemplate.convertAndSend("VIRTUALCARD_EXCHANGE", "VIRTUALCARD_CREATE_VDC_ERROR", employee);
		} catch (Exception e) {
		} 
//        this.rabbitTemplate.send("VIRTUALCARD_CREATE_VDC_ERROR", failedMessage);
    }
	
	@RabbitListener(queues = "VIRTUALCARD_CREATE_VDC")
	public void recievedMessage(Message in) throws InvalidSalaryException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			Employee employee = objectMapper.readValue(new String(in.getBody()), Employee.class);
			logger.info("Recieved Message From RabbitMQ: " + employee);
		} catch (Exception e) {
		} 
		
//		if (employee.getSalary() < 0) {
//			throw new InvalidSalaryException();
//		}
		if (hasExceededRetryCount(in)) {
            putIntoParkingLot(in);
            return;
        }
		throw new InvalidSalaryException("There was an error");
	}
	
	private boolean hasExceededRetryCount(Message in) {
		List<Map<String, ?>> xDeathHeader = in.getMessageProperties().getXDeathHeader();
		if (xDeathHeader != null && xDeathHeader.size() >= 1) {
			Long count = (Long) xDeathHeader.get(0).get("count");
			logger.info("Count: " + count);
			return count >= 3;
		}

		return false;
	}

//	@RabbitListener(queues = "VIRTUALCARD_CREATE_VDC_ERROR")
//	public void recievedMessageDeadLetter(Employee messsage) {
//
//		logger.info("*** Recieved Message From Dead-Letter-Queue: " + messsage);
//	}

//	@RabbitListener(queues = "javainuse.queue")
//	public void recievedMessage(Employee employee) throws InvalidSalaryException {
//		logger.info("Recieved Message From RabbitMQ: " + employee);
//		if (employee.getSalary() < 0) {
//			throw new InvalidSalaryException();
//		}
//	}
//
//	@RabbitListener(queues = "deadLetterQueue")
//	public void recievedMessageDeadLetter(Employee messsage) {
//
//		logger.info("*** Recieved Message From Dead-Letter-Queue: " + messsage);
//	}
	
//	@RabbitListener(queues = "deadLetterQueue")
//    public void recievedMessageDeadLetter(Message messsage) {
//		
//        logger.info("Recieved Message From RabbitMQ: " + messsage);
//    }
	
}