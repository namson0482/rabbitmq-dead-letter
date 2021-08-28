package com.javainuse.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javainuse.model.CardRequest;
import com.javainuse.model.CardServiceRequestDTO;
import com.javainuse.model.Employee;
import com.javainuse.service.RabbitMQSender;
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

	@GetMapping(value = "/producer")
	public String producer(@RequestParam("empName") String empName, @RequestParam("empId") String empId,
			@RequestParam("salary") int salary) {

		Employee emp = new Employee();
		emp.setEmpId(empId);
		emp.setEmpName(empName);
		emp.setSalary(salary);
		rabbitMQSender.send(emp);

		return "Message sent to the RabbitMQ JavaInUse Successfully";
	}

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

		if(wrongExchange==null)
			rabbitMQSender.send(cardRequest);
		else
			rabbitMQSender.send(cardRequest, wrongExchange);
		log.info("Message {}", objectMapper.writeValueAsString(cardRequest));
		return "Message sent to the RabbitMQ JavaInUse Successfully";
	}



}
