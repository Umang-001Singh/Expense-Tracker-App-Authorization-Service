package com.ExpenseTracker.Auth.Service;

import com.ExpenseTracker.Auth.DTO.SignupRequestDto;
import com.ExpenseTracker.Auth.Entities.RefreshToken;
import com.ExpenseTracker.Auth.Response.JWTResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthControllerService {

    private final UserAuthenticationService userAuthenticationService;

    private final RefreshTokenService refreshTokenService;

    private final JWTService jwtService;

    public ResponseEntity signUpService(SignupRequestDto signupRequestDto){
        Boolean isAlreadySignedUp = userAuthenticationService.signUpUser(signupRequestDto);

        if(isAlreadySignedUp.equals(Boolean.FALSE)){
            return new ResponseEntity<>("User Already Exists", HttpStatus.BAD_REQUEST);
        }

        RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(signupRequestDto.getUserName());

        String jwtToken = jwtService.createJWTToken(signupRequestDto.getUserName());

        return new ResponseEntity<>(JWTResponse
                .builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken.getToken())
                .build(), HttpStatus.OK);
    }
}
