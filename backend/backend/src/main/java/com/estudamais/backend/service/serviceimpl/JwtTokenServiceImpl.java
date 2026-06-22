package com.estudamais.backend.service.serviceimpl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.estudamais.backend.entity.User;
import com.estudamais.backend.service.JwtService;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtTokenServiceImpl implements JwtService {
    @Value("${estudamais.jwt.secret}")
    private String secret;
    @Value("${estudamais.jwt.expiration}")
    private Integer expirate;

    @Override
    public String getSubject(String jwt) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("estuda-mais-monorepo")
                    .build()
                    .verify(jwt)
                    .getSubject();
        } catch (JwtException e) {
            throw new RuntimeException("erro ao verificar token" + e);
        }
    }

    @Override
    public String gerarToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("estuda-mais-monorepo")
                    .withSubject(user.getEmail())
                    .withClaim("id",user.getId())
                    .withExpiresAt(Instant.ofEpochSecond(expirate))
                    .sign(algorithm);
        } catch (JwtException e) {
            throw new RuntimeException("erro ao criar token" + e);
        }
    }
}
