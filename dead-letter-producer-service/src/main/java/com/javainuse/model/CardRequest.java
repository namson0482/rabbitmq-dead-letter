package com.javainuse.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.amqp.rabbit.connection.CorrelationData;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardRequest<T> implements Serializable {

    @JsonProperty("requestTypCd")
    private String requestTypCd;

    @JsonProperty("data")
    private T data;

    public CorrelationData getCorrelationData() {
        String uniqueID = data instanceof CardRequestDetail ? ((CardRequestDetail) data).getREQNBR() + "" :
                UUID.randomUUID().toString();
        return new CorrelationData(requestTypCd + "|" + uniqueID);
    }
}
