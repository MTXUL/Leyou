package com.leyou.cart.interceptor;

import com.leyou.auth.properties.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;
import com.leyou.cart.properties.JwtProperties;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Slf4j
public class UserInterceptor implements HandlerInterceptor {
    // 定义一个线程域，存放登录用户
    private static final ThreadLocal <UserInfo> tl = new ThreadLocal <>();

    private JwtProperties jwtProperties;

    public UserInterceptor(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取token
        String token = CookieUtils.getCookieValue(request, jwtProperties.getTokenKey());
        //解析token
        try {
            UserInfo user = JwtUtils.getUserInfo(jwtProperties.getPublicKey(), token);
//            利用线程将user信息传给cart的controller
//            线程存取的对象为map结构，key为当前线程的名字
            tl.set(user);
            return true;
        } catch (Exception e) {
            log.error("【请登录后在访问购物车】：" + e);
            return false;
        }

    }
    public static UserInfo getLoginUser() {
        return tl.get();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        tl.remove();
    }
}


