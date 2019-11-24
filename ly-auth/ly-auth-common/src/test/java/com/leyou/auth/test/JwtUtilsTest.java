package com.leyou.auth.test;



import com.leyou.auth.properties.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;

import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author bystander
 * @date 2018/10/1
 */
public class JwtUtilsTest {

    private static final String publicKeyPath = "D:\\hm\\rsa.pub";
    private static final String privateKeyPath = "D:\\hm\\rsa.pri";

    private PrivateKey privateKey;
    private PublicKey publicKey;


    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(publicKeyPath, privateKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        privateKey = RsaUtils.getPrivateKey(privateKeyPath);
        publicKey = RsaUtils.getPublicKey(publicKeyPath);
    }

    @Test
    public void generateToken() {
        //生成Token
        String s = JwtUtils.generateToken(new UserInfo(20L, "Jack"), privateKey, 5);
        System.out.println("s = " + s);
    }


    @Test
    public void parseToken() {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiSmFjayIsImV4cCI6MTU2NTk0NjU1NX0.XFaw41I_WNKUzppWvNvrSsNjjBZL4YuNdWfuU8RWsRElhAAf084XmtQmYNa5WNOZlBi5YBEU7NjbS-8mG3mWnu8lYmQ8kp4zgK1-8w9bykMU-uvTCRJJqV4-SYGjNiFH9c6-LgJ0VYxIXtNx2Dgs5uL4jJDApnUADafcYZ_LIFM";
        UserInfo userInfo = JwtUtils.getUserInfo(publicKey, token);

        System.out.println("id:" + userInfo.getId());
        System.out.println("name:" + userInfo.getName());
    }

    @Test
    public void parseToken1() {
    }

    @Test
    public void getUserInfo() {
    }

    @Test
    public void getUserInfo1() {
    }
}