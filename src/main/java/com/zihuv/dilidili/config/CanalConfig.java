package com.zihuv.dilidili.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class CanalConfig {

    @Value("${canal.hostname}")
    private String hostname;

    @Value("${canal.port}")
    private Integer port;

    @Value("${canal.destination}")
    private String destination;

    @Value("${canal.username}")
    private String username;

    @Value("${canal.password}")
    private String password;
}