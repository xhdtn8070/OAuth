package org.tikim.boot.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.codec.binary.Base64;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.tikim.boot.domain.entity.User;
import org.tikim.boot.domain.token.Jwt;
import org.tikim.boot.enums.ErrorMessage;
import org.tikim.boot.enums.TokenSubject;
import org.tikim.boot.enums.TokenType;
import org.tikim.boot.exception.NonCriticalException;
import org.tikim.boot.mapper.UserMapper;
import org.tikim.boot.repository.UserRepository;
import org.tikim.boot.service.JwtService;
import org.tikim.boot.service.UserService;
import org.tikim.boot.util.DateUtil;
import org.tikim.boot.util.SesSender;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Resource
    UserRepository userRepository;

    @Resource
    UserMapper userMapper;

    @Resource
    JwtService jwtService;

    @Resource
    private SesSender sesSender;

    @Resource
    private SpringTemplateEngine templateEngine;

    @Value("${ses.domain}")
    private String domain;

    @Value("${project.school-email}")
    private String schoolEmail;

    @Value("${jwt.access-token.time}")
    private int accessTokenTime;

    @Value("${jwt.refresh-token.time}")
    private int refreshTokenTime;

    @Override
    public User getProfile(Long id) {
        return userMapper.selectUser(id);
    }

    @Override
    public User patchPassword(String passwrod) {
        return null;
    }

    @Override
    public User getUserFull(Timestamp valueOf) {
        return null;
    }

    @Override
    public User patchUser(User user) {
        return null;
    }

    @Override
    public User signUp(User user) throws JsonProcessingException {
        /**
         * 유효성 검사
         */
        // account 중복 검사
        if(userMapper.selectUserByAccount(user.getAccount())!=null){
            throw new NonCriticalException(ErrorMessage.USER_ACCOUNT_DUPLICATE);
        }

        // student email 중복 검사 (타인의 메일로 등록하는 경우 대비하기)
        if(userMapper.selectUserByStudentEmail(user.getStudentEmail())!=null){
            throw new NonCriticalException(ErrorMessage.USER_STUDENT_EMAIL_DUPLICATE);
        }

        // 유저 저장
        user.setPassword(BCrypt.hashpw(user.getPassword(),BCrypt.gensalt()));
        user.setSalt(UUID.randomUUID().toString());
        userRepository.save(user);

        // JWT 토큰 1시간 짜리 발행 -salt : user.getSalt()
        String token = jwtService.generateToken(TokenType.DISPOSABLE, TokenSubject.SIGN_UP_AUTHENTICATION, user,1);

        // HOST , JWT 토큰 html로 생성
        // user.getStudentEmail()로 전송
        Context context = new Context();
        context.setVariable("host",((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getHeader("host"));
        context.setVariable("version","v1");
        context.setVariable("token",token);
        String html = templateEngine.process("mail/register_authenticate.html",context);
        sesSender.sendMail("no-reply@" + domain, user.getStudentEmail()+"@" + schoolEmail,"가입 인증 메일",html);

        return user;
    }

    @Override
    public Jwt signIn(User inputUser)
    {
        //아이디 체크
        User user = userMapper.selectUserByAccount(inputUser.getAccount());
        if(user==null){
            throw new NonCriticalException(ErrorMessage.USER_ACCOUNT_NULL_POINTER_EXCEPTION);
        }
        //패스워드 체크
        if(!BCrypt.checkpw(inputUser.getPassword(), user.getPassword())){
            throw new NonCriticalException(ErrorMessage.USER_PASSWORD_INVAILD_EXCEPTION);
        }

        return new Jwt(jwtService.generateToken(TokenType.USER,TokenSubject.ACCESS,user,accessTokenTime),
                jwtService.generateToken(TokenType.USER,TokenSubject.REFRESH,user,refreshTokenTime));
    }

    @Override
    public String authenticate(String token) {
        try {
            Jws<Claims> mail = jwtService.validate(token);
        }catch (Exception e){
            return "실패페이지";
        }
        return "성공페이지";
    }
}
