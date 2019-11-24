package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.multipart.MultipartFile;

@SpringBootApplication
@EnableDiscoveryClient
public class UploadApplication {
    public static void main(String[] args) {
        SpringApplication.run(UploadApplication.class,args);
    }


}
