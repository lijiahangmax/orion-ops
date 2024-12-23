/*
 * Copyright (c) 2021 - present Jiahang Li, All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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
package cn.orionsec.ops.constant.event;

import cn.orionsec.kit.lang.utils.io.FileReaders;
import cn.orionsec.kit.lang.utils.io.FileWriters;
import cn.orionsec.kit.lang.utils.io.Files1;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * 替换版本号
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2024/12/23 10:21
 */
public class ReplaceVersion {

    private static final String TARGET_VERSION = "1.3.1";

    private static final String REPLACE_VERSION = "1.3.2";

    private static final String PATH = new File("").getAbsolutePath();

    private static final String[] DOCKER_FILES = new String[]{
            "docker/push.sh",
            "docker/adminer/build.sh",
            "docker/adminer/build.sh",
            "docker/mysql/build.sh",
            "docker/redis/build.sh",
            "docker/service/build.sh",
            "docker-compose.yml",
    };

    private static final String[] POM_FILES = new String[]{
            "orion-ops-api/pom.xml",
            "orion-ops-api/orion-ops-common/pom.xml",
            "orion-ops-api/orion-ops-dao/pom.xml",
            "orion-ops-api/orion-ops-data/pom.xml",
            "orion-ops-api/orion-ops-mapping/pom.xml",
            "orion-ops-api/orion-ops-model/pom.xml",
            "orion-ops-api/orion-ops-runner/pom.xml",
            "orion-ops-api/orion-ops-service/pom.xml",
            "orion-ops-api/orion-ops-web/pom.xml",
    };

    private static final String APPLICATION_PROP_FILE = "orion-ops-api/orion-ops-web/src/main/resources/application.properties";

    private static final String PACKAGE_JSON_FILE = "orion-ops-vue/package.json";

    public static void main(String[] args) {
        replaceDockerFiles();
        replacePomFiles();
        replaceApplicationProp();
        replacePackageJson();
    }

    /**
     * 替换 docker 文件
     */
    private static void replaceDockerFiles() {
        for (String file : DOCKER_FILES) {
            readAndWrite(file, s -> s.replaceAll(TARGET_VERSION, REPLACE_VERSION));
        }
    }

    /**
     * 替换 pom 文件
     */
    private static void replacePomFiles() {
        for (String file : POM_FILES) {
            readAndWrite(file, s -> s.replaceAll("<version>" + TARGET_VERSION + "</version>", "<version>" + REPLACE_VERSION + "</version>"));
        }
    }

    /**
     * 替换 application.properties 文件
     */
    private static void replaceApplicationProp() {
        readAndWrite(APPLICATION_PROP_FILE, s -> s.replaceAll("app.version=" + TARGET_VERSION, "app.version=" + REPLACE_VERSION));
    }

    /**
     * 替换 package.json 文件
     */
    private static void replacePackageJson() {
        readAndWrite(PACKAGE_JSON_FILE, s -> s.replaceAll("\"version\": \"" + TARGET_VERSION + "\"", "\"version\": \"" + REPLACE_VERSION + "\""));
    }

    /**
     * 读取并且写入
     *
     * @param path    path
     * @param mapping mapping
     */
    private static void readAndWrite(String path, Function<String, String> mapping) {
        String filePath = Files1.getPath(PATH, path);
        try {
            // 读取文件内容
            byte[] bytes = FileReaders.readAllBytesFast(filePath);
            // 写入文件内容
            FileWriters.writeFast(filePath, mapping.apply(new String(bytes)).getBytes(StandardCharsets.UTF_8));
            System.out.println("OK:  " + path);
        } catch (Exception e) {
            System.err.println("ERR: " + path);
        }
    }

}
