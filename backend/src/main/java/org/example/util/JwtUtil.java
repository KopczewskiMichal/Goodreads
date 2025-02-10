package org.example.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;


@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(long userId, boolean isAdmin) {
        JSONObject json = new JSONObject();
        json.put("userId", userId);
        json.put("isAdmin", isAdmin);
        String jsonString = json.toString();
        return Jwts.builder()
                .setSubject(jsonString)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid JWT token");
        }
    }

    public long getUserIdFromRequest(HttpServletRequest request) {
        String jwt = Arrays.stream((request).getCookies() != null ? (request).getCookies() : new Cookie[0])
                .filter(cookie -> "JWT".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
        if (jwt != null) {
            String tokenPayload = validateToken(jwt);
            JSONObject payload = new JSONObject(tokenPayload);
            return payload.getLong("userId");
        } else throw new IllegalArgumentException("Invalid JWT token");
    }


}

