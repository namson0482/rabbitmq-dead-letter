package com.sonvu.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonvu.model.CardRequest;
import com.sonvu.service.RabbitMQSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/javainuse-rabbitmq/")
@Slf4j
public class RabbitMQWebController {

	@Autowired
	RabbitMQSender rabbitMQSender;

	@Autowired
	private ObjectMapper objectMapper;

	@PostMapping(value = "/card_service")
	public String producer(@RequestBody CardRequest cardRequest) throws JsonProcessingException {

		rabbitMQSender.send(cardRequest);
		log.info("Message {}", objectMapper.writeValueAsString(cardRequest));
		return "Message sent to the RabbitMQ JavaInUse Successfully";
	}

	@PostMapping(value = { "/test_service", "/test_service/{wrongExchange}" })
	@ResponseBody
	public String producer(@RequestBody CardRequest cardRequest,
						   @PathVariable(name = "wrongExchange",required = false) String wrongExchange) throws JsonProcessingException {

		if(wrongExchange==null) rabbitMQSender.send(cardRequest);
		else rabbitMQSender.send(cardRequest, wrongExchange);
		log.info("Message {}", objectMapper.writeValueAsString(cardRequest));
		return "Message sent to the RabbitMQ JavaInUse Successfully";
	}
}
