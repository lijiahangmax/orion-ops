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
package cn.orionsec.ops.constant.app;

import lombok.Getter;

/**
 * 操作类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/6 18:57
 */
@Getter
public enum ActionType {

    /**
     * 构建-检出代码
     */
    BUILD_CHECKOUT(110, StageType.BUILD),

    /**
     * 构建-机器命令
     */
    BUILD_COMMAND(120, StageType.BUILD),

    /**
     * 发布-传输产物
     */
    RELEASE_TRANSFER(210, StageType.RELEASE),

    /**
     * 发布-机器命令
     */
    RELEASE_COMMAND(220, StageType.RELEASE),

    ;

    private final Integer type;

    private final StageType stage;

    private final Integer stageType;

    ActionType(Integer type, StageType stage) {
        this.type = type;
        this.stage = stage;
        this.stageType = stage.getType();
    }

    public static ActionType of(Integer type) {
        if (type == null) {
            return null;
        }
        for (ActionType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return null;
    }

    public static ActionType of(Integer type, Integer stageType) {
        if (type == null) {
            return null;
        }
        for (ActionType value : values()) {
            if (value.type.equals(type) && value.stageType.equals(stageType)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 是否是构建action
     *
     * @param type type
     * @return res
     */
    public static boolean isBuildAction(Integer type) {
        if (type == null) {
            return false;
        }
        for (ActionType value : values()) {
            if (value.type.equals(type)) {
                return StageType.BUILD.equals(value.stage);
            }
        }
        return false;
    }

    /**
     * 是否是发布action
     *
     * @param type type
     * @return res
     */
    public static boolean isReleaseAction(Integer type) {
        if (type == null) {
            return false;
        }
        for (ActionType value : values()) {
            if (value.type.equals(type)) {
                return StageType.RELEASE.equals(value.stage);
            }
        }
        return false;
    }

}
