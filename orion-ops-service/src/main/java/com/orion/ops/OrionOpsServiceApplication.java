package com.orion.ops;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * @author ljh15
 */
@SpringBootApplication
@ImportResource(locations = {"classpath:config/spring-*.xml"})
@MapperScan("com.orion.ops.dao")
public class OrionOpsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrionOpsServiceApplication.class, args);
    }

}
