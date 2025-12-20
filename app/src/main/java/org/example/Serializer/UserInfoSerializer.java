package org.example.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;
import org.example.eventProducer.UserInfoEvent;

@Slf4j
public class UserInfoSerializer implements Serializer<UserInfoEvent> {

    @Override
    public byte[] serialize(String s, UserInfoEvent userInfoEvent) {
        byte[] returnValue = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            returnValue = objectMapper.writeValueAsString(userInfoEvent).getBytes();
        }
        catch (Exception ex){
            log.error("Error serializing UserInfoRequestDto ", ex);
        }
        return returnValue;
    }
}
