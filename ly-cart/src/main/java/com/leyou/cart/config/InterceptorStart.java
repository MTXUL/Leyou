package com.leyou.cart.config;

import com.leyou.cart.interceptor.UserInterceptor;
import com.leyou.cart.properties.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.jws.soap.SOAPBinding;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class InterceptorStart implements WebMvcConfigurer {

//    为了让springMVC将jwtProperties注入
    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInterceptor(jwtProperties)).addPathPatterns("/**");
    }
}
