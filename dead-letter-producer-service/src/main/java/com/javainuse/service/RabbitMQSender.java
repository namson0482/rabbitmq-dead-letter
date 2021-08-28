package com.javainuse.service;

import com.javainuse.config.properties.AppProperties;
import com.javainuse.model.CardRequest;
import com.javainuse.model.CardServiceRequestDTO;
import com.javainuse.model.CorrelationDataWithMessage;
import com.javainuse.model.Employee;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RabbitMQSender {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQSender.class);


    private final AppProperties appProperties;

    private RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMQSender(RabbitTemplate rabbitTemplate, AppProperties appProperties) {

        this.rabbitTemplate = rabbitTemplate;
        this.appProperties = appProperties;
        setupCallbacks();
    }

    private void setupCallbacks() {

        rabbitTemplate.setMandatory(true);
        /*
         * Confirms/returns enabled in application.properties - add the callbacks here.
         */
        this.rabbitTemplate.setConfirmCallback((correlation, ack, reason) -> {
            if (correlation != null) {
                log.info("ConfirmCallBack received " + (ack ? " ack " : " nack ") + "for correlation: " + correlation);
            }
        });

        this.rabbitTemplate.setReturnsCallback(returned -> {
            String bodyMsg = new String(returned.getMessage().getBody());
            log.info("Returned: " + bodyMsg + "\nreplyCode: " + returned.getReplyCode()
                    + "\nreplyText: " + returned.getReplyText() + "\nexchange/rk: "
                    + returned.getExchange() + "/" + returned.getRoutingKey());
        });

        /*
         * Replace the correlation data with one containing the converted message in case
         * we want to resend it after a nack.
         */
        rabbitTemplate
                .setCorrelationDataPostProcessor((message, correlationData) -> new CorrelationDataWithMessage(
                        correlationData != null ? correlationData.getId() : null, message));
    }

    public void send(Employee employee) {

        rabbitTemplate.convertAndSend(appProperties.getRabbitmq().getExchange(),
                appProperties.getRabbitmq().getCreateVdcQueue(), employee, employee.getCorrelationData());
        logger.info("Send msg = " + employee);

    }



    public void send(CardRequest cardRequest, String... wrongExchange) {
        if (wrongExchange.length == 1) {
            String exchange = appProperties.getRabbitmq().getExchange();
            String routingKey = appProperties.getRabbitmq().getCreateVdcQueue() + "_new";
            if (wrongExchange[0].equalsIgnoreCase("both")) {
                exchange += "_new";
            }
            rabbitTemplate.convertAndSend(exchange, routingKey, cardRequest, cardRequest.getCorrelationData());
        } else {
            rabbitTemplate.convertAndSend(appProperties.getRabbitmq().getExchange(),
                    appProperties.getRabbitmq().getCreateVdcQueue(), cardRequest, cardRequest.getCorrelationData());
        }
        logger.info("Send msg = " + cardRequest);
    }

}