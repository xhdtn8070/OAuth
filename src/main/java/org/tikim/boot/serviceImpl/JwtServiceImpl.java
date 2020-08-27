package org.tikim.boot.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tikim.boot.domain.entity.DisposableSalt;
import org.tikim.boot.domain.entity.User;
import org.tikim.boot.enums.ErrorMessage;
import org.tikim.boot.enums.TokenSubject;
import org.tikim.boot.enums.TokenType;
import org.tikim.boot.exception.NonCriticalException;
import org.tikim.boot.mapper.DisposableSaltMapper;
import org.tikim.boot.mapper.UserMapper;
import org.tikim.boot.repository.DisposableSaltRepository;
import org.tikim.boot.service.JwtService;
import org.tikim.boot.util.DateUtil;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static org.tikim.boot.enums.ErrorMessage.*;

/**
 * https://tansfil.tistory.com/59
 */
@Service
@Transactional
public class JwtServiceImpl implements JwtService {

    @Resource
    UserMapper userMapper;

    @Resource
    DisposableSaltMapper disposableSaltMapper;

    @Resource
    DisposableSaltRepository disposableSaltRepository;

    @Override
    public String generateToken(TokenType type, TokenSubject subject, User user, int hours) {
        if(type==null||subject==null||user==null){
            throw new NonCriticalException(JWT_NULL_POINTER_EXCEPTION);
        }
        String salt = getSalt(type,subject,user,true,true);
        return generateToken(type,subject,user,hours,salt);
    }
    private String generateToken(TokenType type, TokenSubject subject, User user, int hours,String salt) {

        Date exp = DateUtil.addHoursToJavaUtilDate(new Date(),hours);
        String Token = Jwts.builder()
                .setSubject(subject.toString())
                .claim("id",user.getId())
                .claim("type",type.toString())
                .claim("hours",hours)
                .setExpiration(exp)
                .setIssuedAt(new Date())
                .signWith(Keys.hmacShaKeyFor(salt.getBytes()), SignatureAlgorithm.HS256)
                .compact();
        return Token;
    }
    @Override
    public Jws<Claims> validate(String token) {
        Map<String, Object> map = validateFormat(token);

        User user = userMapper.selectUser(Long.parseLong(map.get("id").toString()));
        if (user == null || user.getDeletes().equals(true)) {
            throw new NonCriticalException(ErrorMessage.USER_NULL_POINTER_EXCEPTION);
        }
        TokenType type = TokenType.valueOf(map.get("type").toString());
        TokenSubject subject = TokenSubject.valueOf(map.get("subject").toString());
        if(type==null||subject==null){
            throw new NonCriticalException(ErrorMessage.JWT_PAYLOAD_EXCEPTION);
        }
        String salt = getSalt(type,subject,user,false,false);
        
        /** parseClaimJws 에러 형태
         1) ExpiredJwtException : JWT를 생성할 때 지정한 유효기간 초과할 때.
         2) UnsupportedJwtException : 예상하는 형식과 일치하지 않는 특정 형식이나 구성의 JWT일 때
         3) MalformedJwtException : JWT가 올바르게 구성되지 않았을 때
         4) SignatureException :  JWT의 기존 서명을 확인하지 못했을 때
         5) IllegalArgumentException
         */

        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(salt.getBytes()).parseClaimsJws(token);
//        if (!claimsJws.getBody().getSubject().equals(subject)) {
//            throw new NonCriticalException(ErrorMessage.JWT_SUBJECT_INVALID_EXCEPTION);
//        }

        return claimsJws;
    }

    @Override
    public String reGenerateToken(String token) {
        Map<String, Object> map = validateFormat(token);
        TokenType type = TokenType.valueOf(map.get("type").toString());
        TokenSubject subject = TokenSubject.valueOf(map.get("subject").toString());
        User user = userMapper.selectUser(Long.parseLong(map.get("id").toString()));
        int hours = Integer.parseInt(map.get("hours").toString());
        if(type==null||subject==null||user==null){
            throw new NonCriticalException(JWT_NULL_POINTER_EXCEPTION);
        }

        String salt = getSalt(type, subject, user,false, true);
        return generateToken(type,subject, user, hours,salt);
    }
    private Map<String,Object> validateFormat(String token){
        if (token == null ){
            throw new NonCriticalException(JWT_NULL_POINTER_EXCEPTION);
        }

        String[] strings = token.split("\\.");
        if(strings.length!=3){
            throw new NonCriticalException(ErrorMessage.JWT_FORMAT_EXCEPTION);
        }
        Map<String,Object> map = null;
        try{
            map = new ObjectMapper().readValue(new String(Base64.decodeBase64(strings[1])), Map.class);
            if(map.get("sub")==null||map.get("id")==null||map.get("exp")==null||map.get("iat")==null||map.get("hours")==null||map.get("type")==null){
                throw new NonCriticalException(ErrorMessage.JWT_PAYLOAD_EXCEPTION);
            }

        } catch (Exception e){
            throw new NonCriticalException(ErrorMessage.JWT_FORMAT_EXCEPTION);
        }
        return map;
    }
    private String getSalt(TokenType type, TokenSubject subject, User user, boolean create, boolean update){
        if(type==null||subject==null){
            throw new NonCriticalException(ErrorMessage.JWT_PAYLOAD_EXCEPTION);
        }
        if(type.equals(TokenType.USER)){
            return user.getSalt();
        }
        if(type.equals(TokenType.REUSABLE)){
            return "";
        }
        if(type.equals(TokenType.DISPOSABLE)){
            DisposableSalt disposableSalt = disposableSaltMapper.selectDisposableSaltByUnique(user.getId(),subject.toString());
            if(disposableSalt==null){
                if(create){
                    disposableSalt = new DisposableSalt();
                    disposableSalt.setUserId(user.getId());
                    disposableSalt.setSubject(subject.toString());
                    disposableSalt.setSalt(UUID.randomUUID().toString());
                    disposableSaltRepository.save(disposableSalt);
                    return disposableSalt.getSalt();
                }
                throw new NonCriticalException(UNDEFINED_EXCEPTION);

            }else{
                if(update){
                    disposableSalt.setSalt(UUID.randomUUID().toString());
                    disposableSaltRepository.save(disposableSalt);
                }
                return disposableSalt.getSalt();
            }

        }
        throw new NonCriticalException(JWT_PAYLOAD_EXCEPTION);
    }
}
