package org.example.Controllers;

import lombok.AllArgsConstructor;
import org.example.Entities.RefreshToken;
import org.example.Entities.UserInfo;
import org.example.Repository.RefreshTokenRepository;
import org.example.Repository.UserRepository;
import org.example.Requests.AuthorizationRequestDto;
import org.example.Requests.RefreshTokenRequestDto;
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
    private RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/authorization/v1/login")
    public ResponseEntity AuthenticateAndLogin(@RequestBody AuthorizationRequestDto authenticationRequestDto){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequestDto.getUserName(), authenticationRequestDto.getPassword()));

        if(authentication.isAuthenticated()){
            String jwtToken = jwtService.createJWTToken(authenticationRequestDto.getUserName());
            String userName = authenticationRequestDto.getUserName();
            UserInfo user = userRepository.findByUserName(userName);
            Optional<RefreshToken> oldRefreshToken = refreshTokenService.findByUserId(user.getUserId());

            if(oldRefreshToken.isEmpty() || refreshTokenService.verifyExpiry(oldRefreshToken.get())){
                RefreshToken newRefreshToken = refreshTokenService.createOrUpdateRefreshToken(authenticationRequestDto.getUserName());

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

    @PostMapping("authorization/v1/refreshToken")
    public ResponseEntity refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto){
        Optional<RefreshToken> oldToken = refreshTokenRepository.findByToken(refreshTokenRequestDto.getRefreshToken());
        if(!oldToken.isEmpty() && !refreshTokenService.verifyExpiry(oldToken.get())){
            UserInfo user = oldToken.get().getUserInfo();
            String jwtToken = jwtService.createJWTToken(user.getUserName());
            return new ResponseEntity<>(JWTResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(oldToken.get().getToken())
                    .build(), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Please login again!", HttpStatus.BAD_REQUEST);
        }
    }

}
