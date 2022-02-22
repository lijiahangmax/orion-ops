package com.orion.ops.runner;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.consts.app.BuildStatus;
import com.orion.ops.consts.app.StageType;
import com.orion.ops.dao.ApplicationBuildDAO;
import com.orion.ops.entity.domain.ApplicationBuildDO;
import com.orion.ops.service.api.ApplicationActionLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

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
public class CleanBuildStatusRunner implements CommandLineRunner {

    @Resource
    private ApplicationBuildDAO applicationBuildDAO;

    @Resource
    private ApplicationActionLogService applicationActionLogService;

    @Override
    public void run(String... args) {
        log.info("重置应用构建状态-开始");
        Wrapper<ApplicationBuildDO> wrapper = new LambdaQueryWrapper<ApplicationBuildDO>()
                .in(ApplicationBuildDO::getBuildStatus, BuildStatus.WAIT.getStatus(), BuildStatus.RUNNABLE.getStatus());
        List<ApplicationBuildDO> buildList = applicationBuildDAO.selectList(wrapper);
        for (ApplicationBuildDO build : buildList) {
            // 修改状态
            Long buildId = build.getId();
            ApplicationBuildDO update = new ApplicationBuildDO();
            update.setId(buildId);
            update.setBuildStatus(BuildStatus.TERMINATED.getStatus());
            update.setBuildEndTime(new Date());
            update.setUpdateTime(new Date());
            applicationBuildDAO.updateById(update);
            applicationActionLogService.resetActionStatus(buildId, StageType.BUILD);
            log.info("重置应用构建状态-执行 {}", buildId);
        }
        log.info("重置应用构建状态-结束");
    }

}
