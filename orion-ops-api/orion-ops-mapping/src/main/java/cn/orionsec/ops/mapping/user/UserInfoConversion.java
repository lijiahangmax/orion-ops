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
package cn.orionsec.ops.mapping.user;

import cn.orionsec.kit.lang.utils.convert.TypeStore;
import cn.orionsec.kit.lang.utils.time.Dates;
import cn.orionsec.ops.entity.domain.UserInfoDO;
import cn.orionsec.ops.entity.dto.user.UserDTO;
import cn.orionsec.ops.entity.vo.user.UserInfoVO;

import java.util.Date;
import java.util.Optional;

/**
 * 用户信息 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 18:24
 */
public class UserInfoConversion {

    static {
        TypeStore.STORE.register(UserInfoDO.class, UserInfoVO.class, d -> {
            UserInfoVO vo = new UserInfoVO();
            vo.setId(d.getId());
            vo.setUsername(d.getUsername());
            vo.setNickname(d.getNickname());
            vo.setRole(d.getRoleType());
            vo.setStatus(d.getUserStatus());
            vo.setLocked(d.getLockStatus());
            vo.setPhone(d.getContactPhone());
            vo.setEmail(d.getContactEmail());
            Date lastLoginTime = d.getLastLoginTime();
            vo.setLastLoginTime(lastLoginTime);
            Optional.ofNullable(lastLoginTime).map(Dates::ago).ifPresent(vo::setLastLoginAgo);
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(UserDTO.class, UserInfoVO.class, d -> {
            UserInfoVO vo = new UserInfoVO();
            vo.setId(d.getId());
            vo.setUsername(d.getUsername());
            vo.setNickname(d.getNickname());
            vo.setRole(d.getRoleType());
            vo.setStatus(d.getUserStatus());
            return vo;
        });
    }

}
