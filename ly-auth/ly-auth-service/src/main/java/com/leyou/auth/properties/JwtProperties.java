package com.leyou.auth.properties;

import com.leyou.auth.utils.RsaUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("ly.jwt")
@Slf4j
public class JwtProperties {
    private String secret;
//    保存在磁盘中，因此每次创建实例后，需要从磁盘中读取
    private String pubKeyPath;
    private String priKeyPath;
    private int expire;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    @PostConstruct
    public void initJwtProperties(){
//        还要判断私钥公钥是否存在,不存在则要创建
        try {
            if(!new File(pubKeyPath).exists() || !new File(priKeyPath).exists()){
                RsaUtils.generateKey(pubKeyPath,priKeyPath,secret);
            }
                // 获取公钥和私钥

            publicKey = RsaUtils.getPublicKey(pubKeyPath);
            privateKey = RsaUtils.getPrivateKey(priKeyPath);
        } catch (Exception e) {
            log.error("【创建公钥私钥失败】："+e);
            throw new LyException(ExceptionEnum.CREATED_KEY_ERROR);
        }


    }

}
