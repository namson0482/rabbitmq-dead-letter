package com.sonvu.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonvu.model.CardRequest;
import com.sonvu.model.message.WebHookMessage;
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

	@PostMapping(value = "/webhook")
	public String pushWebhook(@RequestBody CardRequest cardRequest) throws JsonProcessingException {

		rabbitMQSender.send(cardRequest);
		log.info("Message {}", objectMapper.writeValueAsString(cardRequest));
		return "Message sent to the RabbitMQ JavaInUse Successfully";
	}

	@PostMapping(value = "/card_service")
	public String producer(@RequestBody CardRequest cardRequest) throws JsonProcessingException {

		rabbitMQSender.send(cardRequest);

		log.info("Message {}", objectMapper.writeValueAsString(cardRequest));
		return "Message sent to the RabbitMQ JavaInUse Successfully";
	}

	private void startNewThread(CardRequest cardRequest) {
		new Thread(() -> {
			for(int i=1; i <= 200; i++) {
				try {
					rabbitMQSender.send(cardRequest);
					Thread.sleep(100);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@PostMapping(value = "/card_service_loop")
	public String producerWithLoop(@RequestBody CardRequest cardRequest) throws JsonProcessingException {

		startNewThread(cardRequest);
		startNewThread(cardRequest);
		log.info("Message {}", objectMapper.writeValueAsString(cardRequest));
		return "Message sent to the RabbitMQ JavaInUse Successfully";
	}

	@PostMapping(value = { "/test_service", "/test_service/{wrongExchange}" })
	@ResponseBody
	public String producerWithPublishConfirm(@RequestBody CardRequest cardRequest,
						   @PathVariable(name = "wrongExchange",required = false) String wrongExchange) throws JsonProcessingException {

		if(wrongExchange==null) rabbitMQSender.send(cardRequest);
		else rabbitMQSender.send(cardRequest, wrongExchange);
		log.info("Message {}", objectMapper.writeValueAsString(cardRequest));
		return "Message sent to the RabbitMQ JavaInUse Successfully";
	}

	@PostMapping(value = "/card_message")
	public String producer(@RequestBody WebHookMessage message) throws JsonProcessingException {

		rabbitMQSender.send(message);
		log.info("Message {}", objectMapper.writeValueAsString(message));
		return "Message sent to the RabbitMQ Successfully";
	}
}
