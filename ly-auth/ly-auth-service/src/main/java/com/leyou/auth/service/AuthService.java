package com.leyou.auth.service;


import com.leyou.auth.clients.UserClients;
import com.leyou.auth.properties.JwtProperties;
import com.leyou.auth.properties.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.pojo.User;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


import org.springframework.stereotype.Service;

@Slf4j
@EnableConfigurationProperties(JwtProperties.class)
@Service
public class AuthService {
    @Autowired
    private UserClients userClients;
    @Autowired
    private JwtProperties jwtProperties;

    public String createdToken(String username, String password ) {
        //首先判断用户名和密码是否正确
        User user = userClients.queryUserNameAndPassword(username, password);
        if(user==null){
            throw new LyException(ExceptionEnum.USERNAME_PASSWORD_ERROR);
        }
        //        然后生成令牌
        String token = JwtUtils.generateToken(new UserInfo(user.getId(), username), jwtProperties.getPrivateKey(), jwtProperties.getExpire());
        if (StringUtils.isBlank(token)){
            throw new LyException(ExceptionEnum.CREATED_TOKEN_ERROR);
        }
        return token;
    }
    public UserInfo verifyToken(String token) {
        try {
            return JwtUtils.getUserInfo(jwtProperties.getPublicKey(), token);
        } catch (Exception e) {
            log.error("【解析token失败】"+e);
            throw new LyException(ExceptionEnum.VERIFY_TOKEN_ERROR);
        }
    }
}
