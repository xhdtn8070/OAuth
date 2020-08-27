package org.tikim.boot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.tikim.boot.domain.entity.User;
import org.tikim.boot.domain.token.Jwt;

import java.sql.Timestamp;

public interface UserService {
    public User getProfile(Long id);

    public User patchPassword(String passwrod);

    public User getUserFull(Timestamp valueOf);

    public User patchUser(User user);

    public User signUp(User user) throws JsonProcessingException;

    public Jwt signIn(User user);

    public String authenticate(String accessToken);
}
