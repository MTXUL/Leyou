package com.leyou.auth.web;

import com.leyou.auth.properties.JwtProperties;
import com.leyou.auth.properties.UserInfo;
import com.leyou.auth.service.AuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtProperties jwtProperties;


    @PostMapping("login")
    public ResponseEntity<Void> createdToken(@RequestParam("username")String username, @RequestParam("password")String password, HttpServletRequest request, HttpServletResponse response){
        String token= authService.createdToken(username, password);
        CookieUtils.newBuilder(response).httpOnly().request(request).build("LY_TOKEN",token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("verify")
    public ResponseEntity<UserInfo> verifyToken(@CookieValue("LY_TOKEN")String token,HttpServletRequest request, HttpServletResponse response){
        UserInfo userInfo = authService.verifyToken(token);
        //刷新token防止 用户在浏览是过了30分钟直接退出
        String newToken= JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
        //httpOnly防止js修改
        CookieUtils.newBuilder(response).httpOnly().request(request).build("LY_TOKEN",token);
        return ResponseEntity.ok(authService.verifyToken(token));
    }

}
