package org.example.Serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;
import org.example.DTO.UserInfoRequestDto;

@Slf4j
public class UserInfoSerializer implements Serializer<UserInfoRequestDto> {

    @Override
    public byte[] serialize(String s, UserInfoRequestDto userInfoRequestDto) {
        byte[] returnValue = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            returnValue = objectMapper.writeValueAsString(userInfoRequestDto).getBytes();
        }
        catch (Exception ex){
            log.error("Error serializing UserInfoRequestDto", ex);
        }
        return returnValue;
    }
}
