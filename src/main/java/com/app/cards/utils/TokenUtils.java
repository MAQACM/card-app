package com.app.cards.utils;



import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.auth0.jwt.JWT;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.app.cards.security.*;


@Component
public class TokenUtils {

    private static final Logger logger = Logger.getLogger(TokenUtils.class.getName());
    public static final String ROLES = "role";
    @Value("${cards.ms.signKey}")
    private String secret;
    @Value("${cards.ms.tokenExpiry}")
    private long tokenExpiry;
    private Key key;
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }
    private Boolean isTokenExpired(String token) {
        final Date expiration = getClaim(token).getExpiresAt();
        return expiration.before(new Date());
    }
    public  List<Map<String,String>> getRoles(String token){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get(ROLES,List.class);
    }
    public String doGenerateToken(UserDetails userDetails) {
        try{
        Map<String,Object>claims=new HashMap<>(1);
        claims.put(ROLES,userDetails.getAuthorities());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject((userDetails.getUsername()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiry * 1000))
                .signWith(key).compact();
        }catch(Exception ex){
            throw new JwtException("error creating token:"+ex.getMessage());
        }
    }

    public static UserDetailsImpl createUserFromAuthentication(Authentication authentication) {
        return UserDetailsImpl.builder()
                .email((String) authentication.getPrincipal())
                .authorities(authentication.getAuthorities())
                .build();
    }

    //validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        String username = getClaim(token).getSubject();
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public static DecodedJWT getClaim(String token){
        try{
            return JWT.decode(token);
        }catch(Exception ex){
            throw new RuntimeException("Invalid token");
        }
    }

}
