package com.leyou.order.config;

import com.leyou.common.utils.IdWorker;
import com.leyou.order.properties.IdWorkerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(IdWorkerProperties.class)
public class IdWorkerConfig {
    @Bean
    public IdWorker idWorker(IdWorkerProperties idWorkerProperties) {
        return new IdWorker(idWorkerProperties.getWorkerId(), idWorkerProperties.getDatacenterId());
    }
}
