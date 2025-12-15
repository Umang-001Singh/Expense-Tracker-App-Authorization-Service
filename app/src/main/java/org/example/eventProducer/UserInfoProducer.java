package org.example.eventProducer;

import org.example.DTO.UserInfoRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class UserInfoProducer {
    private final KafkaTemplate<String, UserInfoRequestDto> kafkaTemplate;

    @Value("${spring.kafka.topic.name}")
            private String TOPIC_NAME;

    UserInfoProducer(KafkaTemplate<String, UserInfoRequestDto> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEventToKafka(UserInfoRequestDto userInfoRequestDto){
        Message<UserInfoRequestDto> message = MessageBuilder
                .withPayload(userInfoRequestDto)
                .setHeader(KafkaHeaders.TOPIC, TOPIC_NAME)
                .build();
    }
}
