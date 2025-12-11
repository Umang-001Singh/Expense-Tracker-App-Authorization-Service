package org.example.Service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.Entities.RefreshToken;
import org.example.Entities.UserInfo;
import org.example.Repository.RefreshTokenRepository;
import org.example.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;


@Service
@AllArgsConstructor
public class RefreshTokenService {

    private UserRepository userRepository;

    private RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(String username) {
        UserInfo user = userRepository.findByUserName(username);

        RefreshToken token = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plus(30*2, ChronoUnit.DAYS))
                .userInfo(user)
                .build();

        return refreshTokenRepository.save(token);
    }

    public RefreshToken verifyExpiry(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now())<0){
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getTokenId() + ": Token Expired. Please login to continue!");
        }
        return token;
    }

    public RefreshToken findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }
}
