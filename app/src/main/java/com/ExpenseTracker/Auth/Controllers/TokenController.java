package com.ExpenseTracker.Auth.Controllers;

import com.ExpenseTracker.Auth.Service.TokenControllerService;
import jakarta.validation.Valid;
import com.ExpenseTracker.Auth.Requests.AuthorizationRequestDto;
import com.ExpenseTracker.Auth.Requests.RefreshTokenRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final TokenControllerService tokenControllerService;

    @PostMapping("/authorization/v1/login")
    public ResponseEntity AuthenticateAndLogin(@Valid @RequestBody AuthorizationRequestDto authenticationRequestDto){

        return tokenControllerService.AuthenticateAndLoginService(authenticationRequestDto);
    }

    @PostMapping("authorization/v1/refreshToken")
    public ResponseEntity refreshToken(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequestDto){

        return tokenControllerService.refreshTokenImpl(refreshTokenRequestDto);
    }

}
