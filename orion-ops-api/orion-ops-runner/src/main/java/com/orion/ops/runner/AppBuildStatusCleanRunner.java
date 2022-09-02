package com.orion.ops.runner;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.constant.app.ActionStatus;
import com.orion.ops.constant.app.BuildStatus;
import com.orion.ops.constant.app.StageType;
import com.orion.ops.dao.ApplicationActionLogDAO;
import com.orion.ops.dao.ApplicationBuildDAO;
import com.orion.ops.entity.domain.ApplicationActionLogDO;
import com.orion.ops.entity.domain.ApplicationBuildDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 清空构建状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/6 18:03
 */
@Component
@Order(2300)
@Slf4j
public class AppBuildStatusCleanRunner implements CommandLineRunner {

    @Resource
    private ApplicationBuildDAO applicationBuildDAO;

    @Resource
    private ApplicationActionLogDAO applicationActionLogDAO;

    @Override
    public void run(String... args) {
        log.info("重置应用构建状态-开始");
        // 更新构建状态
        Wrapper<ApplicationBuildDO> buildWrapper = new LambdaQueryWrapper<ApplicationBuildDO>()
                .in(ApplicationBuildDO::getBuildStatus, BuildStatus.WAIT.getStatus(), BuildStatus.RUNNABLE.getStatus());
        ApplicationBuildDO updateBuild = new ApplicationBuildDO();
        updateBuild.setBuildStatus(BuildStatus.TERMINATED.getStatus());
        updateBuild.setUpdateTime(new Date());
        applicationBuildDAO.update(updateBuild, buildWrapper);

        // 更新操作状态
        LambdaQueryWrapper<ApplicationActionLogDO> actionWrapper = new LambdaQueryWrapper<ApplicationActionLogDO>()
                .eq(ApplicationActionLogDO::getStageType, StageType.BUILD.getType())
                .in(ApplicationActionLogDO::getRunStatus, ActionStatus.WAIT.getStatus(), ActionStatus.RUNNABLE.getStatus());
        ApplicationActionLogDO updateAction = new ApplicationActionLogDO();
        updateAction.setRunStatus(ActionStatus.TERMINATED.getStatus());
        updateAction.setUpdateTime(new Date());
        applicationActionLogDAO.update(updateAction, actionWrapper);
        log.info("重置应用构建状态-结束");
    }

}
