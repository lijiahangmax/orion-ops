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
package com.orion.ops.service.api;

import com.orion.ops.entity.dto.user.UserDTO;
import com.orion.ops.entity.request.user.UserLoginRequest;
import com.orion.ops.entity.request.user.UserResetRequest;
import com.orion.ops.entity.vo.user.UserLoginVO;

/**
 * 认证服务类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/1 16:58
 */
public interface PassportService {

    /**
     * 登录
     *
     * @param request request
     * @return wrapper
     */
    UserLoginVO login(UserLoginRequest request);

    /**
     * 登出
     */
    void logout();

    /**
     * 重置密码
     *
     * @param request request
     */
    void resetPassword(UserResetRequest request);

    /**
     * 通过token获取用户 所有通过token查用户都要用此方法
     *
     * @param token   token
     * @param checkIp 检查的ip 不检查设置为null
     * @return user
     */
    UserDTO getUserByToken(String token, String checkIp);

}
