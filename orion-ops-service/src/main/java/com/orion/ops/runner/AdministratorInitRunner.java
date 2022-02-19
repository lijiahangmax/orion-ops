package com.orion.ops.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.id.UUIds;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.user.RoleType;
import com.orion.ops.dao.UserInfoDAO;
import com.orion.ops.entity.domain.UserInfoDO;
import com.orion.ops.utils.ValueMix;
import com.orion.utils.crypto.Signatures;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 管理员初始化器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 18:05
 */
@Component
@Order(1400)
@Slf4j
public class AdministratorInitRunner implements CommandLineRunner {

    private static final String USERNAME = "orionadmin";

    private static final String NICKNAME = "管理员";

    private static final String PASSWORD = Signatures.md5(USERNAME);

    @Resource
    private UserInfoDAO userInfoDAO;

    @Override
    public void run(String... args) {
        // 用户名重复检查
        LambdaQueryWrapper<UserInfoDO> wrapper = new LambdaQueryWrapper<UserInfoDO>()
                .eq(UserInfoDO::getUsername, USERNAME);
        UserInfoDO admin = userInfoDAO.selectOne(wrapper);
        if (admin != null) {
            return;
        }
        // 密码
        String salt = UUIds.random19();
        String password = ValueMix.encPassword(PASSWORD, salt);
        // 创建
        UserInfoDO insert = new UserInfoDO();
        insert.setUsername(USERNAME);
        insert.setNickname(NICKNAME);
        insert.setPassword(password);
        insert.setSalt(salt);
        insert.setRoleType(RoleType.ADMINISTRATOR.getType());
        insert.setUserStatus(Const.ENABLE);
        userInfoDAO.insert(insert);
        log.info("默认管理员用户已初始化");
    }

}
