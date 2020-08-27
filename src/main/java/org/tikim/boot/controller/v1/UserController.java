package org.tikim.boot.controller.v1;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.tikim.boot.annotation.Auth;
import org.tikim.boot.annotation.ValidationGroups;
import org.tikim.boot.annotation.Xss;
import org.tikim.boot.domain.entity.User;
import org.tikim.boot.domain.token.Jwt;
import org.tikim.boot.service.UserService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@RestController("UserControllerV1")
@RequestMapping("/v1/user")
public class UserController {
    @Resource
    UserService userService;

    @Auth
    @ApiOperation(value = "", authorizations = {@Authorization(value="Bearer +accessToken")})
    @GetMapping(value = "/profile/{id}")
    public ResponseEntity getProfile(
            @ApiParam(name = "id",value = "유저 아이디 (예시:1)", required = true) @PathVariable(value = "id",required = true) Long id)
    {
        return new ResponseEntity(userService.getProfile(id), HttpStatus.OK);
    }

    @Xss
    @Auth(type = Auth.Type.NONE)
    @PostMapping(path = "/signin")
    public ResponseEntity<Jwt> postSignin(@ApiParam(value = "(required: account, password)", required = true) @RequestBody @Validated(ValidationGroups.Login.class) User user) throws Exception {
        return new ResponseEntity<Jwt>(userService.signIn(user),HttpStatus.OK);
    }

    @Xss
    @Auth(type = Auth.Type.NONE)
    @PostMapping(path = "/signup")
    public ResponseEntity<User> postSignup(@ApiParam(value = "(required: account, password)", required = true) @RequestBody @Validated(ValidationGroups.Create.class) User user) throws Exception {
        return new ResponseEntity<User>(userService.signUp(user),HttpStatus.OK);
    }

    @Xss
    @Auth(type = Auth.Type.NONE)
    @GetMapping(path = "/signup/authenticate")
    public ResponseEntity<String> authenticate(
            @ApiParam(value = "(required: token)",name = "token", required = true) @RequestParam(required = true,name = "token") String token) throws Exception {
        return new ResponseEntity<String>(userService.authenticate(token),HttpStatus.OK);
    }

//    @Xss
//    @Auth(type = Auth.Type.NONE)
//    @PostMapping(path = "/authenticate")
//    public ResponseEntity<Jwt> authenticate(@Valid @RequestBody(required = true) Jwt jwt) throws Exception {
//        return new ResponseEntity<Jwt>(userService.,HttpStatus.OK);
//    }

    @Xss
    @Auth
    @PatchMapping(path = "/me/password")
    @ApiOperation(value = "", authorizations = {@Authorization(value="Bearer +accessToken")})
    public ResponseEntity<User> mePassword(@ApiParam(value = "(required:password)", required = true) @RequestBody String passwrod) throws Exception {
        return new ResponseEntity<User>(userService.patchPassword(passwrod),HttpStatus.OK);
    }

    @GetMapping(path = "/me")
    @Auth
    @ApiOperation(value = "", authorizations = {@Authorization(value="Bearer +accessToken")})
    public ResponseEntity<User> getMe(
            @ApiParam(value = "(required: timestamps), defalut = now(), ex) 2020-01-02 10:50:50") @RequestParam(value="timestamps",required = false,defaultValue = "#{T(java.time.LocalDateTime).now()}") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime timestamps) {
        return new ResponseEntity<User>(userService.getUserFull(Timestamp.valueOf(timestamps)),HttpStatus.OK);
    }

    @Xss
    @Auth
    @PatchMapping(path = "/me")
    @ApiOperation(value = "", authorizations = { @Authorization(value="Bearer +accessToken")})
    public ResponseEntity<User> meUserUpdate(@ApiParam(value = "(required:password)", required = true) @RequestBody @Validated(ValidationGroups.Update.class) User user) throws Exception {
        return new ResponseEntity<User>(userService.patchUser(user),HttpStatus.OK);
    }

}
