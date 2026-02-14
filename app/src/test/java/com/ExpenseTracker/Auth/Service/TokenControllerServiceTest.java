package com.ExpenseTracker.Auth.Service;


import com.ExpenseTracker.Auth.Entities.RefreshToken;
import com.ExpenseTracker.Auth.Entities.UserInfo;
import com.ExpenseTracker.Auth.Repository.RefreshTokenRepository;
import com.ExpenseTracker.Auth.Repository.UserRepository;
import com.ExpenseTracker.Auth.Requests.AuthorizationRequestDto;
import com.ExpenseTracker.Auth.Requests.RefreshTokenRequestDto;
import com.ExpenseTracker.Auth.Response.JWTResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenControllerServiceTest {

    private AuthorizationRequestDto authorizationRequestDto;
    private UserInfo mockUser;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JWTService jwtService;

    @Mock
    UserRepository userRepository;

    @Mock
    RefreshTokenService refreshTokenService;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Mock
    Authentication authentication;

    @InjectMocks
    TokenControllerService tokenControllerService;

    @BeforeEach
    void setUp(){
        authorizationRequestDto = new AuthorizationRequestDto("test_user_01", "TestUser@01");
        mockUser = new UserInfo();
        mockUser.setUserId("01_TU");
        mockUser.setUserName("test_user_01");
    }

    @Test
    public void AuthenticateAndLoginService_success_NewTokenCreated(){

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(authentication.isAuthenticated())
                .thenReturn(true);

        when(jwtService.createJWTToken(authorizationRequestDto.getUserName()))
                .thenReturn("01_TU_mock_jwt");

        when(userRepository.findByUserName(authorizationRequestDto.getUserName()))
                .thenReturn(mockUser);

        when(refreshTokenService.findByUserId(mockUser.getUserId()))
                .thenReturn(Optional.empty());

//        First condition: New user, creating new RefreshToken
        RefreshToken mockNewRefreshToken = RefreshToken.builder().token("01_TU_new-refresh-token").build();

        when(refreshTokenService.createOrUpdateRefreshToken(authorizationRequestDto.getUserName()))
                .thenReturn(mockNewRefreshToken);

        ResponseEntity<?> response = tokenControllerService.AuthenticateAndLoginService(authorizationRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JWTResponse body = (JWTResponse) response.getBody();
        assertNotNull(body);
        assertEquals("01_TU_mock_jwt", body.getAccessToken());
        assertEquals("01_TU_new-refresh-token", body.getRefreshToken());
    }


    @Test
    public void AuthenticateAndLoginService_success_ExistingValidToken(){

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(authentication.isAuthenticated())
                .thenReturn(true);

        when(jwtService.createJWTToken(authorizationRequestDto.getUserName()))
                .thenReturn("02_TU_mock_jwr");

        when(userRepository.findByUserName(authorizationRequestDto.getUserName()))
                .thenReturn(mockUser);

        RefreshToken existingToken = RefreshToken.builder().token("existing-token").build();

        when(refreshTokenService.findByUserId(mockUser.getUserId()))
                .thenReturn(Optional.of(existingToken));

        when(refreshTokenService.verifyExpiry(existingToken))
                .thenReturn(false);

        ResponseEntity<?> response = tokenControllerService.AuthenticateAndLoginService(authorizationRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JWTResponse body = (JWTResponse) response.getBody();
        assertNotNull(body);
        assertEquals("02_TU_mock_jwr", body.getAccessToken());
        assertEquals("existing-token", body.getRefreshToken());
    }

    @Test
    public void AuthenticateAndLoginService_Failure_Unauthenticated(){

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(authentication.isAuthenticated())
                .thenReturn(false);

        ResponseEntity<?> response = tokenControllerService.AuthenticateAndLoginService(authorizationRequestDto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error in Authentication Service", response.getBody());
    }

    @Test
    public void refreshTokenImpl_Success(){
        RefreshTokenRequestDto request = new RefreshTokenRequestDto("refresh-valid-token");

        RefreshToken oldToken = RefreshToken.builder().token("refresh-valid-token").userInfo(mockUser).build();

        when(refreshTokenRepository.findByToken(request.getRefreshToken()))
                .thenReturn(Optional.of(oldToken));

        when(refreshTokenService.verifyExpiry(oldToken))
                .thenReturn(false);

        when(jwtService.createJWTToken(mockUser.getUserName()))
                .thenReturn("jwt-valid-token");

        ResponseEntity<?> response = tokenControllerService.refreshTokenImpl(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JWTResponse body = (JWTResponse) response.getBody();
        assertNotNull(body);
        assertEquals("jwt-valid-token", body.getAccessToken());
    }

    @Test
    public void refreshTokenImpl_Failure_Missing(){

        RefreshTokenRequestDto request = new RefreshTokenRequestDto("refresh-missing-token");

        RefreshToken oldToken = RefreshToken.builder().token("refresh-missing-token").userInfo(mockUser).build();

        when(refreshTokenRepository.findByToken(oldToken.getToken()))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = tokenControllerService.refreshTokenImpl(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Please login again!", response.getBody());
    }

    @Test
    public void refreshTokenImpl_failure_Expired(){

        RefreshTokenRequestDto request = new RefreshTokenRequestDto("refresh-expired-token");

        RefreshToken oldToken = RefreshToken.builder().token("refresh-expired-token").userInfo(mockUser).build();

        when(refreshTokenRepository.findByToken(request.getRefreshToken()))
                .thenReturn(Optional.of(oldToken));

        when(refreshTokenService.verifyExpiry(oldToken))
                .thenReturn(true);

        ResponseEntity<?> response = tokenControllerService.refreshTokenImpl(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Please login again!", response.getBody());
    }
}
