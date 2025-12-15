package org.example.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.example.Entities.UserInfo;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserInfoRequestDto extends UserInfo {
    private String firstName;
//    private String middleName;
    private String lastName;
    private Long phoneNumber;
    private String email;
}
