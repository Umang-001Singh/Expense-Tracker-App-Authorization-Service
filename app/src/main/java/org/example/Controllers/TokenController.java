package org.example.Controllers;

import lombok.AllArgsConstructor;
import org.example.DTO.UserSignupRequestDto;
import org.example.Entities.RefreshToken;
import org.example.Entities.UserInfo;
import org.example.Repository.UserRepository;
import org.example.Response.JWTResponse;
import org.example.Service.JWTService;
import org.example.Service.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@AllArgsConstructor
public class TokenController {
    private JWTService jwtService;
    private RefreshTokenService refreshTokenService;
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;

    @PostMapping("/authorization/v1/login")
    public ResponseEntity AuthenticateAndLogin(@RequestBody UserSignupRequestDto userSignupRequestDto){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userSignupRequestDto.getUserName(), userSignupRequestDto.getPassword()));

        if(authentication.isAuthenticated()){
            String jwtToken = jwtService.createJWTToken(userSignupRequestDto.getUserName());
            String userName = userSignupRequestDto.getUserName();
            UserInfo user = userRepository.findByUserName(userName);
            Optional<RefreshToken> oldRefreshToken = refreshTokenService.findByUserId(user.getUserId());

            if(oldRefreshToken.isEmpty() || refreshTokenService.verifyExpiry(oldRefreshToken.get())){
                RefreshToken newRefreshToken = refreshTokenService.createOrUpdateRefreshToken(userSignupRequestDto.getUserName());

                return new ResponseEntity<>(JWTResponse
                        .builder()
                        .accessToken(jwtToken)
                        .refreshToken(newRefreshToken.getToken())
                        .build(), HttpStatus.OK);
            }


            return new ResponseEntity<>(JWTResponse
                    .builder()
                    .accessToken(jwtToken)
                    .refreshToken(oldRefreshToken.get().getToken())
                    .build(), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Error in Authentication Service", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
