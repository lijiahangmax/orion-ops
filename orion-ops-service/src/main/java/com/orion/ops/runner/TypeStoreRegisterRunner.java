package com.orion.ops.runner;

import com.orion.ops.OrionOpsServiceApplication;
import com.orion.support.Attempt;
import com.orion.utils.reflect.PackageScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * TypeStore加载器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/5/27 18:55
 */
@Component
@Order(200)
@Slf4j
public class TypeStoreRegisterRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        log.info("注册vo转换器-开始");
        new PackageScanner("com.orion.ops.entity.vo")
                .with(OrionOpsServiceApplication.class)
                .scan()
                .getClasses()
                .forEach(Attempt.rethrows(s -> {
                    log.info("register runner init class: {}", s);
                    Class.forName(s.getName());
                }));
        log.info("注册vo转换器-结束");
    }

}
