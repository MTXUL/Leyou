package com.leyou.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
@Data
@ConfigurationProperties(prefix = "ly.filter")
public class AllowPath {
    private List<String> allowPaths;
}
