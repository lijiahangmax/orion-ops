package com.orion.ops.mapping.user;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.entity.domain.UserInfoDO;
import com.orion.ops.entity.dto.user.UserDTO;
import com.orion.ops.entity.vo.user.UserInfoVO;

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
