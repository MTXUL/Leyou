package com.leyou.filters;

import com.leyou.auth.properties.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import com.leyou.properties.AllowPath;
import com.leyou.properties.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Component
@EnableConfigurationProperties({JwtProperties.class, AllowPath.class})
public class AuthFilters extends ZuulFilter {
    @Autowired
    private JwtProperties properties;
    @Autowired
    private AllowPath allowPath;
//    过滤器类型
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

//    过滤顺序
    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER-1;
    }
//    是否拦截
    @Override
    public boolean shouldFilter() {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        //获取的请求uri
        String requestURI = request.getRequestURI();
        return !isFilterPath(requestURI);
    }

    private boolean isFilterPath(String requestURI) {
//        判断请求头是否已允许的路径开头
        List <String> allowPaths = allowPath.getAllowPaths();
        for (String path : allowPaths) {
            if (requestURI.startsWith(path)) {
                return true;
            }
        }
        return false;
    }

    //拦截逻辑
    @Override
    public Object run() throws ZuulException {
        //获取context对象
        RequestContext currentContext = RequestContext.getCurrentContext();
//        获取request
        HttpServletRequest request = currentContext.getRequest();
//        从request中获取cookie
        String token = CookieUtils.getCookieValue(request, properties.getTokenKey());
        try {
//            没异常放行，什么度不做
            JwtUtils.getUserInfo(properties.getPublicKey(), token);
            //todo 权限校验
        } catch (Exception e) {
            log.error("【解析token失败已被拦截】："+e);
            // 校验出现异常，返回403
            currentContext.setSendZuulResponse(false);
            currentContext.setResponseStatusCode(403);
        }
        return null;
    }
}
