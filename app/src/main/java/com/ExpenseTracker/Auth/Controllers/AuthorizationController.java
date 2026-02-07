package com.ExpenseTracker.Auth.Controllers;

import com.ExpenseTracker.Auth.DTO.SignupRequestDto;
import com.ExpenseTracker.Auth.Service.AuthControllerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthorizationController {

    private final AuthControllerService authControllerService;

    @PostMapping("/authorization/v1/signup")
    public ResponseEntity signUp(@Valid @RequestBody SignupRequestDto signupRequestDto){

        return authControllerService.signUpService(signupRequestDto);
    }
}
