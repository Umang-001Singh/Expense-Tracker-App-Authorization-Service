package org.example.Controllers;

import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.example.DTO.UserSignupRequestDto;
import org.example.Entities.RefreshToken;
import org.example.Response.JWTResponse;
import org.example.Service.JWTService;
import org.example.Service.RefreshTokenService;
import org.example.Service.UserAuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TokenController {
    private JWTService jwtService;
    private RefreshTokenService refreshTokenService;
    private AuthenticationManager authenticationManager;

    @PostMapping("/authorization/v1/login")
    public ResponseEntity AuthenticateAndLogin(@RequestBody UserSignupRequestDto userSignupRequestDto){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userSignupRequestDto.getUserName(), userSignupRequestDto.getPassword()));

        if(authentication.isAuthenticated()){
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userSignupRequestDto.getUserName());
            String jwtToken = jwtService.createJWTToken(userSignupRequestDto.getUserName());

            return new ResponseEntity<>(JWTResponse
                    .builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken.getToken())
                    .build(), HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Error in Authentication Service", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
