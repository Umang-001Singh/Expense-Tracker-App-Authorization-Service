package com.ExpenseTracker.Auth.Service;

import lombok.AllArgsConstructor;
import com.ExpenseTracker.Auth.Entities.RefreshToken;
import com.ExpenseTracker.Auth.Entities.UserInfo;
import com.ExpenseTracker.Auth.Repository.RefreshTokenRepository;
import com.ExpenseTracker.Auth.Repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;


@Service
@AllArgsConstructor
public class RefreshTokenService {

    private UserRepository userRepository;

    private RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createOrUpdateRefreshToken(String username) {
        UserInfo user = userRepository.findByUserName(username);
        Optional<RefreshToken> oldRefreshToken = refreshTokenRepository.findByUserInfo_UserId(user.getUserId());
        RefreshToken token = null;
        if(oldRefreshToken.isEmpty()) {
             token = RefreshToken.builder()
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plus(60, ChronoUnit.DAYS))
                    .userInfo(user)
                    .build();

             return refreshTokenRepository.save(token);
        }
        else if (oldRefreshToken.isPresent() && verifyExpiry(oldRefreshToken.get())){
            token = oldRefreshToken.get();
            token.setToken(UUID.randomUUID().toString());
            token.setExpiryDate(Instant.now().plus(60, ChronoUnit.DAYS));
            return refreshTokenRepository.save(token);
        }
        return oldRefreshToken.get();
    }

    public boolean verifyExpiry(RefreshToken token){
//        If token has expired, return true, else return false.
        if(token.getExpiryDate().compareTo(Instant.now())<0){
            return true;
        }
        return false;
    }

    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(token);
    }

    public Optional<RefreshToken> findByUserId(String userId) { return refreshTokenRepository.findByUserInfo_UserId(userId); }
}
