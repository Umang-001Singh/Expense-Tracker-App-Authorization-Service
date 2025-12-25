package org.example.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.DTO.UserInfoRequestDto;
import org.example.Entities.Role;
import org.example.Entities.UserInfo;
import org.example.Repository.UserRepository;
import org.example.eventProducer.UserInfoEvent;
import org.example.eventProducer.UserInfoProducer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class UserAuthenticationService implements UserDetailsService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private UserInfoProducer userInfoProducer;

    public UserInfo checkIfUserAlreadyExists(UserInfoRequestDto userInfoRequestDto){
        return userRepository.findByUserName(userInfoRequestDto.getUserName());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Entering into loadUserByUsername");
        UserInfo user = userRepository.findByUserName(username);
        if(user == null){
            log.debug("User not found!");
            throw new UsernameNotFoundException("Could not find the user!");
        }
        return new PrincipalUser(user);
    }

    public boolean signUpUser(UserInfoRequestDto userInfoRequestDto) {
        if(Objects.nonNull(userRepository.findByUserName((userInfoRequestDto.getUserName())))){
            return false;
        }

        String userId = UUID.randomUUID().toString();
        userInfoRequestDto.setPassword(passwordEncoder.encode(userInfoRequestDto.getPassword()));
        userRepository.save(new UserInfo(userId, userInfoRequestDto.getUserName(), userInfoRequestDto.getPassword(), new HashSet<Role>()));
        userInfoRequestDto.setUserId(userId);
        // Push Event to Queue
        userInfoProducer.sendEventToKafka(getUserInfoEventToPublish(userInfoRequestDto));

        return true;
    }

    public UserInfoEvent getUserInfoEventToPublish(UserInfoRequestDto userInfoRequestDto){
        return UserInfoEvent.builder()
                .firstName(userInfoRequestDto.getFirstName())
                .lastName(userInfoRequestDto.getLastName())
                .email(userInfoRequestDto.getEmail())
                .phoneNumber(userInfoRequestDto.getPhoneNumber())
                .userId(userInfoRequestDto.getUserId())
                .build();
    }
}
