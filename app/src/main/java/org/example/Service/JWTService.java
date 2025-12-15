package org.example.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.example.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
@Data
public class JWTService {

    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.secret}")
    private String SECRET;

    public SecretKey getSignKey(){
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }


    // Create JWT
    public String createJWTToken(String username){
        Map<String, Object> claims = new HashMap<>();

        return createJWTTokenHelper(claims, username);
    }

    public String createJWTTokenHelper(Map<String, Object> extraClaims, String username){
        return Jwts.builder()
                .subject(username)
                .claims(extraClaims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000*60*1))
                .signWith(getSignKey())
                .compact();
    }

    // Validate JWT coming from clients
    public Boolean validateJWTToken(String token, UserDetails userDetails){
        final String username = extractUserName(token);

        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public Boolean isTokenExpired(String token){
        Date date = extractExpiryDate(token);
        return date.before(new Date());
    }

    public String extractUserName(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiryDate(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        Claims claims = extractClaimsHelper(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractClaimsHelper(String token){
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
