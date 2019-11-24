package com.leyou.sms.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
@ConfigurationProperties(prefix = "ly.sms")
public class SmsProperties {
    String signName;
    String templateCode;
    String accessKeyId;
    String accessKeySecret;
}