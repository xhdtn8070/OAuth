package org.tikim.boot.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.tikim.boot.domain.entity.User;
import org.tikim.boot.enums.TokenSubject;
import org.tikim.boot.enums.TokenType;


public interface JwtService {

    String generateToken(TokenType type, TokenSubject subject, User user, int hours);

    Jws<Claims> validate(String token);

    String reGenerateToken(String token);
}
