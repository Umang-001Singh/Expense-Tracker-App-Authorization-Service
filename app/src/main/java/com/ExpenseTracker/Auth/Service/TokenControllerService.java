package com.ExpenseTracker.Auth.Service;

import com.ExpenseTracker.Auth.Entities.RefreshToken;
import com.ExpenseTracker.Auth.Entities.UserInfo;
import com.ExpenseTracker.Auth.Repository.RefreshTokenRepository;
import com.ExpenseTracker.Auth.Repository.UserRepository;
import com.ExpenseTracker.Auth.Requests.AuthorizationRequestDto;
import com.ExpenseTracker.Auth.Requests.RefreshTokenRequestDto;
import com.ExpenseTracker.Auth.Response.JWTResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TokenControllerService {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public ResponseEntity<?> AuthenticateAndLoginService(AuthorizationRequestDto authorizationRequestDto){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authorizationRequestDto.getUserName(), authorizationRequestDto.getPassword()));

        if(authentication.isAuthenticated()){
            String jwtToken = jwtService.createJWTToken(authorizationRequestDto.getUserName());
            String userName = authorizationRequestDto.getUserName();
            UserInfo user = userRepository.findByUserName(userName);
            Optional<RefreshToken> oldRefreshToken = refreshTokenService.findByUserId(user.getUserId());

            if(oldRefreshToken.isEmpty() || refreshTokenService.verifyExpiry(oldRefreshToken.get())){
                RefreshToken newRefreshToken = refreshTokenService.createOrUpdateRefreshToken(authorizationRequestDto.getUserName());

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

    public ResponseEntity refreshTokenImpl(RefreshTokenRequestDto refreshTokenRequestDto){
        Optional<RefreshToken> oldToken = refreshTokenRepository.findByToken(refreshTokenRequestDto.getRefreshToken());

        if(!oldToken.isEmpty() && !refreshTokenService.verifyExpiry(oldToken.get())){
            UserInfo user = oldToken.get().getUserInfo();
            String jwtToken = jwtService.createJWTToken(user.getUserName());

            return new ResponseEntity<>(JWTResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(oldToken.get().getToken())
                    .build(), HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("Please login again!", HttpStatus.BAD_REQUEST);
        }
    }
}
