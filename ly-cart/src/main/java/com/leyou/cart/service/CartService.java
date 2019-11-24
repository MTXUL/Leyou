package com.leyou.cart.service;

import com.leyou.auth.properties.UserInfo;
import com.leyou.cart.interceptor.UserInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
public class CartService {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private static final String CART_PREFIX="cart:uid";

    public void addCart(Cart cart) {
        UserInfo user = UserInterceptor.getLoginUser();
        if (user==null) {
            log.error("【请登录后在访问】");
            throw new LyException(ExceptionEnum.USERNAME_PASSWORD_ERROR);
        }
        //记录增加商品的数量;
        Integer num = cart.getNum();
        String key=CART_PREFIX+user.getId();
        String hashKey=cart.getSkuId().toString();
        BoundHashOperations <String, Object, Object> operations = stringRedisTemplate.boundHashOps(key);

        //判断商品是否存在
        if (operations.hasKey(hashKey)) {
            cart = JsonUtils.parse(operations.get(hashKey).toString(), Cart.class);
            // 存在数量加一
            cart.setNum(cart.getNum()+num);
        }
        operations.put(hashKey,JsonUtils.serialize(cart));
    }

    public List<Cart> queryCarts() {
        UserInfo user = UserInterceptor.getLoginUser();
        if (user==null) {
            log.error("【请登录后在访问】");
            throw new LyException(ExceptionEnum.USERNAME_PASSWORD_ERROR);
        }
        String key=CART_PREFIX+user.getId();
        BoundHashOperations <String, Object, Object> operations = stringRedisTemplate.boundHashOps(key);
        List <Cart> carts = operations.values().stream().map(o ->
            JsonUtils.parse(o.toString(), Cart.class)
        ).collect(Collectors.toList());
        return carts;
    }

    public void updateNum(String skuId, Integer num) {
        UserInfo user = UserInterceptor.getLoginUser();
        if (user==null) {
            log.error("【请登录后在访问】");
            throw new LyException(ExceptionEnum.USERNAME_PASSWORD_ERROR);
        }
        String key=CART_PREFIX+user.getId();
        BoundHashOperations <String, Object, Object> operations = stringRedisTemplate.boundHashOps(key);
        String json = operations.get(skuId.toString()).toString();
        Cart cart = JsonUtils.parse(json, Cart.class);
        cart.setNum(num);
        operations.put(skuId.toString(),JsonUtils.serialize(cart));
    }

    public void deleteCart(String id) {
        UserInfo user = UserInterceptor.getLoginUser();
        if (user==null) {
            log.error("【请登录后在访问】");
            throw new LyException(ExceptionEnum.USERNAME_PASSWORD_ERROR);
        }
        String key=CART_PREFIX+user.getId();
        stringRedisTemplate.opsForHash().delete(key,id.toString());
    }
}


