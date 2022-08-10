package com.orion.ops;

import com.orion.lang.support.Attempt;
import com.orion.lang.utils.reflect.PackageScanner;
import org.springframework.beans.factory.InitializingBean;

/**
 * 类型转换注册器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 17:23
 */
public class TypeStoreConversionRegister implements InitializingBean {

    private final String PACKAGE = this.getClass().getPackage().getName() + ".mapping.*";

    @Override
    public void afterPropertiesSet() throws Exception {
        // todo 测试
        new PackageScanner(PACKAGE)
                .with(TypeStoreConversionRegister.class)
                .scan()
                .getClasses()
                .forEach(Attempt.rethrows(s -> {
                    Class.forName(s.getName());
                }));
    }

}
