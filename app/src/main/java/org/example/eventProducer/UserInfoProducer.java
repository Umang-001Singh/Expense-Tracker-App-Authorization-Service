package org.example.eventProducer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserInfoProducer {

    private final KafkaTemplate<String, UserInfoEvent> kafkaTemplate;

    @Value("${spring.kafka.topic-json.name}")
            private String topicJsonName;

    public void sendEventToKafka(UserInfoEvent userInfoEvent){
        Message<UserInfoEvent> message = MessageBuilder
                .withPayload(userInfoEvent)
                .setHeader(KafkaHeaders.TOPIC, topicJsonName)
                .build();

        kafkaTemplate.send(message);
    }
}
