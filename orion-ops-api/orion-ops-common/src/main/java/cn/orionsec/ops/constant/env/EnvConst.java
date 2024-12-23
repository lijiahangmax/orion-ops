/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
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
package cn.orionsec.ops.constant.env;

/**
 * 环境变量常量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/30 10:30
 */
public class EnvConst {

    private EnvConst() {
    }

    // -------------------- symbol --------------------

    public static final String SYMBOL = "@";

    // -------------------- prefix --------------------

    public static final String MACHINE_PREFIX = "machine.";

    public static final String APP_PREFIX = "app.";

    public static final String BUILD_PREFIX = "build.";

    public static final String RELEASE_PREFIX = "release.";

    public static final String SYSTEM_PREFIX = "system.";

    // -------------------- machine --------------------

    public static final String MACHINE_ID = "machine_id";

    public static final String MACHINE_NAME = "machine_name";

    public static final String MACHINE_TAG = "machine_tag";

    public static final String MACHINE_HOST = "machine_host";

    public static final String MACHINE_PORT = "machine_port";

    public static final String MACHINE_USERNAME = "machine_username";

    // -------------------- app --------------------

    public static final String APP_ID = "app_id";

    public static final String APP_NAME = "app_name";

    public static final String APP_TAG = "app_tag";

    public static final String PROFILE_ID = "profile_id";

    public static final String PROFILE_NAME = "profile_name";

    public static final String PROFILE_TAG = "profile_tag";

    // -------------------- build --------------------

    public static final String BUILD_ID = "build_id";

    public static final String BUILD_SEQ = "build_seq";

    public static final String REPO_HOME = "repo_home";

    public static final String REPO_EVENT_HOME = "repo_event_home";

    public static final String BRANCH = "branch";

    public static final String COMMIT = "commit";

    public static final String BUNDLE_PATH = "bundle_path";

    // -------------------- release --------------------

    public static final String RELEASE_ID = "release_id";

    public static final String RELEASE_TITLE = "release_title";

    public static final String TRANSFER_PATH = "transfer_path";

    // -------------------- tail --------------------

    public static final String OFFSET = "offset";

    public static final String FILE = "file";

    // -------------------- scp --------------------

    public static final String TARGET_USERNAME = "target_username";

    public static final String TARGET_HOST = "target_host";

    /**
     * 获取替换变量
     *
     * @param name name
     * @return command
     */
    public static String getReplaceVariable(String name) {
        return SYMBOL + "{" + name + "}";
    }

}
