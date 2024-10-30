/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops;

import com.orion.lang.support.Attempt;
import com.orion.lang.utils.reflect.PackageScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 类型转换注册器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 17:23
 */
@Component
@Slf4j
public class MappingConversionProvider implements InitializingBean {

    private final String CONVERSION_PACKAGE = this.getClass().getPackage().getName() + ".mapping.*";

    private static final String EXPORTER_PACKAGE = "cn.orionsec.ops.entity.exporter";

    private static final String IMPORTER_PACKAGE = "cn.orionsec.ops.entity.importer";

    @Override
    public void afterPropertiesSet() throws Exception {
        new PackageScanner(CONVERSION_PACKAGE, EXPORTER_PACKAGE, IMPORTER_PACKAGE)
                .with(MappingConversionProvider.class)
                .with(DataModuleConversionProvider.class)
                .scan()
                .getClasses()
                .forEach(Attempt.rethrows(s -> {
                    log.info("register type conversion {}", s.getName());
                    Class.forName(s.getName());
                }));
    }

}
