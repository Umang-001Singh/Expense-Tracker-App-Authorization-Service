package org.example.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;
import org.example.eventProducer.UserInfoEvent;

import java.util.Map;

@Slf4j
public class UserInfoSerializer implements Serializer<UserInfoEvent> {

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> map, boolean b) {
    }

    @Override
    public byte[] serialize(String s, UserInfoEvent userInfoEvent) {
        try{
            return objectMapper.writeValueAsString(userInfoEvent).getBytes();
        }
        catch (Exception ex){
            log.error("Error serializing UserInfoRequestDto ", ex);
            return null;
        }
    }

    @Override public void close() {
    }
}
