package org.tikim.boot.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tikim.boot.domain.entity.DisposableSalt;
import org.tikim.boot.domain.entity.User;
import org.tikim.boot.enums.ErrorMessage;
import org.tikim.boot.enums.TokenSubject;
import org.tikim.boot.enums.TokenType;
import org.tikim.boot.exception.CriticalException;
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
        if (type == null || subject == null || user == null) {
            throw new NonCriticalException(JWT_NULL_POINTER_EXCEPTION);
        }
        String salt = getSalt(type, subject, user, true);
        return generateToken(type, subject, user, hours, salt);
    }

    private String generateToken(TokenType type, TokenSubject subject, User user, int hours, String salt) {

        // set Expire time to hours
        Date exp = DateUtil.addHoursToJavaUtilDate(new Date(), hours);
        String Token = Jwts.builder()
                .setSubject(subject.toString())
                .claim("id", user.getId())
                .claim("type", type.toString())
                .claim("hours", hours)
                // 토큰의 만료일을 잡는다.
                .setExpiration(exp)
                // 토큰의 생성일을 잡는다.
                .setIssuedAt(new Date())
                .signWith(Keys.hmacShaKeyFor(salt.getBytes()), SignatureAlgorithm.HS256)
                .compact();
        return Token;
    }

    // 유효한 토큰인지 확인한다.
    @Override
    public Jws<Claims> validate(String token) {
        Map<String, Object> map = validateFormat(token);

        User user = userMapper.selectUser(Long.parseLong(map.get("id").toString()));
        if (user == null || user.getDeletes().equals(true)) {
            throw new NonCriticalException(ErrorMessage.USER_NULL_POINTER_EXCEPTION);
        }
        TokenType type = TokenType.valueOf(map.get("type").toString());
        // 기존에 sub로 지정되어있는 항목을 subject로 받아와서 문제 발생
        TokenSubject subject = TokenSubject.valueOf(map.get("sub").toString());
        if (type == null || subject == null) {
            throw new NonCriticalException(ErrorMessage.JWT_PAYLOAD_EXCEPTION);
        }
        String salt = getSalt(type, subject, user, false);

        /** parseClaimJws 에러 형태
         1) ExpiredJwtException : JWT를 생성할 때 지정한 유효기간 초과할 때.
         2) UnsupportedJwtException : 예상하는 형식과 일치하지 않는 특정 형식이나 구성의 JWT일 때
         3) MalformedJwtException : JWT가 올바르게 구성되지 않았을 때
         4) SignatureException :  JWT의 기존 서명을 확인하지 못했을 때
         5) IllegalArgumentException
         */
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(salt.getBytes()).parseClaimsJws(token);
            return claimsJws;
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (MalformedJwtException e) {
            e.printStackTrace();
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
        } catch (UnsupportedJwtException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        throw new NonCriticalException(UNDEFINED_EXCEPTION);
    }

    // Refresh Token을 받아서
    // Token 자체를 재발급 하는 경우 (exp) - Salt 변경 X
    // Salt값을 변경하는 경우 - 기존 Token 무효화 (비밀번호 변경과 같은 회원정보의 변경)
    @Override
    public String reGenerateToken(String token) {
        Map<String, Object> map = validateFormat(token);
        TokenType type = TokenType.valueOf(map.get("type").toString());
        TokenSubject subject = TokenSubject.valueOf(map.get("sub").toString());
        User user = userMapper.selectUser(Long.parseLong(map.get("id").toString()));
        int hours = Integer.parseInt(map.get("hours").toString());
        if (type == null || subject == null || user == null) {
            throw new NonCriticalException(JWT_NULL_POINTER_EXCEPTION);
        }

        String salt = getSalt(type, subject, user, true);
        return generateToken(type, subject, user, hours, salt);
    }

    @Override
    public TokenType getTokenType(String token) {
        Map<String, Object> map = validateFormat(token);
        TokenType type = TokenType.valueOf(map.get("type").toString());
        return type;
    }

    // Token의 Payload를 decode해서 반환
    private Map<String, Object> validateFormat(String token) {
        if (token == null) {
            throw new NonCriticalException(JWT_NULL_POINTER_EXCEPTION);
        }

        String[] strings = token.split("\\.");
        if (strings.length != 3) {
            throw new NonCriticalException(ErrorMessage.JWT_FORMAT_EXCEPTION);
        }
        Map<String, Object> map = null;
        try {
            map = new ObjectMapper().readValue(new String(Base64.decodeBase64(strings[1])), Map.class);
            if (map.get("sub") == null || map.get("id") == null || map.get("exp") == null || map.get("iat") == null || map.get("hours") == null || map.get("type") == null) {
                throw new NonCriticalException(ErrorMessage.JWT_PAYLOAD_EXCEPTION);
            }

        } catch (Exception e) {
            throw new NonCriticalException(ErrorMessage.JWT_FORMAT_EXCEPTION);
        }
        return map;
    }


    // 기존 정보를 토대로 가져오는 것
    //      ~~가져만 오는 경우~~ -
    //      가져온 후 Update하는 경우 - 기존 정보 만료 ex) 이메일 인증 버튼을 눌렀을 때
    //
    // 새로운 값을 생성하는 것
    //      회원가입
    private String getSalt(TokenType type, TokenSubject subject, User user, boolean create) {
        if (type == null || subject == null) {
            throw new NonCriticalException(ErrorMessage.JWT_PAYLOAD_EXCEPTION);
        }
        if (type.equals(TokenType.USER)) {
            return user.getSalt();
        }
        if (type.equals(TokenType.REUSABLE)) {
            return "";
        }
        if (type.equals(TokenType.DISPOSABLE)) {
            // 회원가입 시 기존에 저장되어있는 정보가 없기 때문에 select시 null이 나온다.
            // DisposableSalt == null일 경우 subject의 @Pattern을 만족하지 않아 진행되지 않는다.
            DisposableSalt disposableSalt = disposableSaltMapper.selectDisposableSaltByUnique(user.getId(), subject.toString());
            if (create) {
                if (disposableSalt == null) {
                    disposableSalt = new DisposableSalt();
                    disposableSalt.setUserId(user.getId());
                    disposableSalt.setSubject(subject.toString());
                }
                disposableSalt.setSalt(UUID.randomUUID().toString());
                disposableSaltRepository.save(disposableSalt);
                return disposableSalt.getSalt();
            } else {
                if (disposableSalt == null) {
                    throw new NonCriticalException(JWT_SALTNULL_EXCEPTION);
                }
                String salt = disposableSalt.getSalt();
                disposableSaltRepository.delete(disposableSalt);
                return salt;
            }
        }
        throw new CriticalException(JWT_NON_TYPE_EXCEPTION);
    }
}

