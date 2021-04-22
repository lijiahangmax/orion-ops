package com.orion.ops;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Jiahang Li
 */
@SpringBootApplication
@ImportResource(locations = {"classpath:config/spring-*.xml"})
@MapperScan("com.orion.ops.dao")
@EnableScheduling
public class OrionOpsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrionOpsServiceApplication.class, args);
    }

}
