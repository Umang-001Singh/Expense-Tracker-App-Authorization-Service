package com.ExpenseTracker.Auth.Service;

import com.ExpenseTracker.Auth.DTO.SignupRequestDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.ExpenseTracker.Auth.Entities.Role;
import com.ExpenseTracker.Auth.Entities.UserInfo;
import com.ExpenseTracker.Auth.Repository.UserRepository;
import com.ExpenseTracker.Auth.eventProducer.UserInfoEvent;
import com.ExpenseTracker.Auth.eventProducer.UserInfoProducer;
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

    public UserInfo checkIfUserAlreadyExists(SignupRequestDto signupRequestDto){
        return userRepository.findByUserName(signupRequestDto.getUserName());
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

    public boolean signUpUser(SignupRequestDto signupRequestDto) {
        if(Objects.nonNull(userRepository.findByUserName((signupRequestDto.getUserName())))){
            return false;
        }

        String userId = UUID.randomUUID().toString();
        signupRequestDto.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
        userRepository.save(new UserInfo(userId, signupRequestDto.getUserName(), signupRequestDto.getPassword(), new HashSet<Role>()));
        signupRequestDto.setUserId(userId);
        // Push Event to Queue
        userInfoProducer.sendEventToKafka(getUserInfoEventToPublish(signupRequestDto));

        return true;
    }

    public UserInfoEvent getUserInfoEventToPublish(SignupRequestDto signupRequestDto){
        return UserInfoEvent.builder()
                .firstName(signupRequestDto.getFirstName())
                .lastName(signupRequestDto.getLastName())
                .email(signupRequestDto.getEmail())
                .phoneNumber(signupRequestDto.getPhoneNumber())
                .userId(signupRequestDto.getUserId())
                .build();
    }
}
