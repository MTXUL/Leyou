package com.leyou.order.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
@ConfigurationProperties(prefix = "ly.work")
public class IdWorkerProperties {
    private long workerId;
    private long datacenterId;
}
