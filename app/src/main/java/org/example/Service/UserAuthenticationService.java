package org.example.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.DTO.UserSignupRequestDto;
import org.example.Entities.Role;
import org.example.Entities.UserInfo;
import org.example.Repository.UserRepository;
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

    UserRepository userRepository;

    PasswordEncoder passwordEncoder;

    public UserInfo checkIfUserAlreadyExists(UserSignupRequestDto userInfo){
        return userRepository.findByUserName(userInfo.getUserName());
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

    public boolean signUpUser(UserSignupRequestDto userInfo) {
        if(Objects.nonNull(userRepository.findByUserName((userInfo.getUserName())))){
            return false;
        }

        String userId = UUID.randomUUID().toString();
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        userRepository.save(new UserInfo(userId, userInfo.getUserName(), userInfo.getPassword(), new HashSet<Role>()));

        //
        return true;
    }
}
