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
package cn.orionsec.ops.service.api;

import cn.orionsec.ops.entity.request.user.UserInfoRequest;
import cn.orionsec.ops.entity.vo.user.UserInfoVO;
import com.orion.lang.define.wrapper.DataGrid;

/**
 * 用户服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/1 16:50
 */
public interface UserService {

    /**
     * 查询用户列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<UserInfoVO> userList(UserInfoRequest request);

    /**
     * 查询用户详情
     *
     * @param request request
     * @return row
     */
    UserInfoVO userDetail(UserInfoRequest request);

    /**
     * 添加用户
     *
     * @param request request
     * @return userId
     */
    Long addUser(UserInfoRequest request);

    /**
     * 更新用户
     *
     * @param request request
     * @return effect
     */
    Integer updateUser(UserInfoRequest request);

    /**
     * 删除用户
     *
     * @param id id
     * @return effect
     */
    Integer deleteUser(Long id);

    /**
     * 更新用户状态
     *
     * @param id     id
     * @param status status
     * @return effect
     */
    Integer updateStatus(Long id, Integer status);

    /**
     * 解锁用户
     *
     * @param id id
     * @return effect
     */
    Integer unlockUser(Long id);

    /**
     * 更新头像
     *
     * @param avatar base64
     * @return effect
     */
    Integer updateAvatar(String avatar);

    /**
     * 自动生成管理员账号
     */
    void generatorDefaultAdminUser();

    /**
     * 重置默认管理员密码
     */
    void resetDefaultAdminUserPassword();

}
