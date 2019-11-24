package com.leyou.upload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "ly.upload")
public class ApplicationProperties {
    private String baseUrl;
    private List <String> allowTypes;
}
