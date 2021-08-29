# rabbitmq-dead-letter
RabbitMQ Dead Letter Example

# Test
1. Producer must send a message in advance. Let it create exchange and routing key. 
2. Send request by **POST** method as URL below:

http://localhost:8080/javainuse-rabbitmq/card_service
```javascript
{
"requestTypCd": "EXCEPTION", 
"data": {
    "REQNBR": 429,
    "REQTRACEID": "47990ce6-d3b4-4bad-8199-a6b5bc2765ae",
    "FULLNAME": "NGUYEN VU"
    }
}
```