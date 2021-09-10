package com.sonvu.service;

import com.sonvu.config.properties.AppProperties;
import com.sonvu.model.CardRequest;
import com.sonvu.model.CorrelationDataWithMessage;
import com.sonvu.model.message.WebHookMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RabbitMQSender {

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

    public void send(CardRequest cardRequest, String... wrongExchange) {
        if (wrongExchange.length == 1) {
            String exchange = appProperties.getRabbitmq().getExchange();
            String routingKey = appProperties.getRabbitmq().getCreateVdcQueue() + "_new";
            if (wrongExchange[0].equalsIgnoreCase("both")) {
                exchange += "_new";
            }
            rabbitTemplate.convertAndSend(exchange, routingKey, cardRequest, cardRequest.getCorrelationData());
        } else {
            try {
                rabbitTemplate.convertAndSend(appProperties.getRabbitmq().getExchange(),
                        appProperties.getRabbitmq().getCreateVdcQueue(), cardRequest, cardRequest.getCorrelationData());
            } catch (AmqpException e) {
                log.info("Connection exception");
            }
        }
        log.info("Send msg = " + cardRequest);
    }

    public void send(WebHookMessage webHookMessage) {
        rabbitTemplate.convertAndSend(appProperties.getRabbitmq().getExchange(),
                appProperties.getRabbitmq().getCreateVdcQueue(), webHookMessage, webHookMessage.getCorrelationData());
        log.info("Send msg = " + webHookMessage);
    }
}