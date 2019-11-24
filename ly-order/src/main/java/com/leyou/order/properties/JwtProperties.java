package com.leyou.order.properties;

import com.leyou.auth.utils.RsaUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("ly.jwt")
@Slf4j
public class JwtProperties {
//    保存在磁盘中，因此每次创建实例后，需要从磁盘中读取
    private String pubKeyPath;
    private String tokenKey;
    private PublicKey publicKey;

    @PostConstruct
    public void initJwtProperties(){
//
        try{
            publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            log.error("【创建公钥私钥失败】："+e);
            throw new LyException(ExceptionEnum.CREATED_KEY_ERROR);
        }


    }

}
