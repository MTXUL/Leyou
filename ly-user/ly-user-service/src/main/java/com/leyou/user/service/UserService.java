package com.leyou.user.service;


import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.item.pojo.User;

import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String KEY_PREFIX="user.verify";




    public Boolean userCheck(String data, Integer type) {
        User recode = new User();
        switch (type){
            case(1):
                recode.setUsername(data);
                break;
            case (2):
                recode.setPhone(data);
                break;
            default:
                throw new LyException(ExceptionEnum.INVAILD_DATA_TYPE);
        }
        return userMapper.selectCount(recode)==0;
    }



    public void smsCode(String phone) {

        HashMap <String, String> map = new HashMap <>();

//        随机生成六位数的验证码
        String code = NumberUtils.generateCode(6);
//        向redis中保存验证码，做校验用
        redisTemplate.opsForValue().set(KEY_PREFIX+phone,code,5, TimeUnit.MINUTES);
        map.put("phone",phone);
        map.put("code",code);
        amqpTemplate.convertAndSend("ly.user.exchange","user.sms.verify",map);
    }



    public void userRegister(@Valid User user, String code) {
        //从redis数据库中获取验证码
        String cacheCode = redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if (!StringUtils.equals(cacheCode,code)) {
            throw new LyException(ExceptionEnum.INVAILD_VERIFY_CODE);
        }
//        然后将数保存到数据库中
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        String MD5password = CodecUtils.md5Hex(user.getPassword(), salt);
        user.setPassword(MD5password);
        user.setCreated(new Date());
        int insert = userMapper.insert(user);
        if(insert!=1){
            throw new LyException(ExceptionEnum.REGISTER_USER_ERROR);
        }
    }

    public User queryUserNameAndPassword(String username, String password) {
        //现根据用户名查询用户
        User user = new User();
        user.setUsername(username);
        User user1 = userMapper.selectOne(user);
//        然后再根据用户的盐，生成相应的密文
        //        判断密文是否一致
        if (!StringUtils.equals(user1.getPassword(), CodecUtils.md5Hex(password,user1.getSalt()))) {
            throw new LyException(ExceptionEnum.USERNAME_PASSWORD_ERROR);
        }


        return user1;

//        返回user
    }
}
