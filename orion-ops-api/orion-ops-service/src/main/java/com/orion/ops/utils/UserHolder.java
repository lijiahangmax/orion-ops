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
package com.orion.ops.utils;

import com.orion.ops.entity.dto.user.UserDTO;

/**
 * 用户信息
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/2 9:44
 */
public class UserHolder {

    private static final ThreadLocal<UserDTO> LOCAL = new ThreadLocal<>();

    public static UserDTO get() {
        return LOCAL.get();
    }

    public static void set(UserDTO user) {
        LOCAL.set(user);
    }

    public static void remove() {
        LOCAL.remove();
    }

}
