package com.lunar.system;

import com.lunar.common.security.annotation.EnableCustomConfig;
import com.lunar.common.swagger.annotation.EnableCustomSwagger2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableCustomSwagger2
@EnableCustomConfig
@EnableScheduling
@SpringBootApplication
public class LunarElderSingleApplication {

    public static void main(String[] args) {
        SpringApplication.run(LunarElderSingleApplication.class, args);
        log.info("启动成功");
    }

}
