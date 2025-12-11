package org.example.Controllers;

import lombok.AllArgsConstructor;
import org.example.DTO.UserSignupRequestDto;
import org.example.Entities.RefreshToken;
import org.example.Response.JWTResponse;
import org.example.Service.JWTService;
import org.example.Service.RefreshTokenService;
import org.example.Service.UserAuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthorizationController {

    private JWTService jwtService;

    private RefreshTokenService refreshTokenService;

    private UserAuthenticationService userAuthenticationService;

    @PostMapping("/authorization/v1/signup")
    public ResponseEntity signUp(@RequestBody UserSignupRequestDto userSignupRequestDto){
        Boolean isAlreadySignedUp = userAuthenticationService.signUpUser(userSignupRequestDto);
        if(Boolean.FALSE.equals(isAlreadySignedUp)){
            return new ResponseEntity<>("User already exists.", HttpStatus.BAD_REQUEST);
        }

        RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(userSignupRequestDto.getUserName());

        String jwtToken = jwtService.createJWTToken(userSignupRequestDto.getUserName());

        return new ResponseEntity<>(JWTResponse
                .builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken.getToken())
                .build(), HttpStatus.OK);
    }
}
