package com.sonvu.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Configuration
@ConfigurationProperties(prefix = "app", ignoreInvalidFields = true)
public class AppProperties {

    private Rabbitmq rabbitmq;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rabbitmq {
        private String exchange;

        private String createVdcQueue;
        private String createVdcQueueRetry;

        private String errorVdcQueue;
        private String errorVdcQueueRetry;

        private int retry;
        private int timeToLive;
    }

}

