package org.example.Repository;

import org.example.Entities.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Integer> {

    public RefreshToken findByToken(String token);
    public Optional<RefreshToken> findByUserInfo_UserId(String userId);
}
