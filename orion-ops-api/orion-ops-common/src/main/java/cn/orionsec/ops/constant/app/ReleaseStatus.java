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

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * app发布状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/11 21:23
 */
@AllArgsConstructor
@Getter
public enum ReleaseStatus {

    /**
     * 待审核
     */
    WAIT_AUDIT(10),

    /**
     * 审核驳回
     */
    AUDIT_REJECT(20),

    /**
     * 待发布
     */
    WAIT_RUNNABLE(30),

    /**
     * 待调度
     */
    WAIT_SCHEDULE(35),

    /**
     * 发布中
     */
    RUNNABLE(40),

    /**
     * 发布完成
     */
    FINISH(50),

    /**
     * 发布停止
     */
    TERMINATED(60),

    /**
     * 发布失败
     */
    FAILURE(70),

    ;

    private final Integer status;

    public static ReleaseStatus of(Integer status) {
        if (status == null) {
            return null;
        }
        for (ReleaseStatus value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

}
