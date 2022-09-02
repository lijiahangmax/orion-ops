package com.orion.ops.runner;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.constant.app.ReleaseStatus;
import com.orion.ops.dao.ApplicationReleaseDAO;
import com.orion.ops.entity.domain.ApplicationReleaseDO;
import com.orion.ops.task.TaskRegister;
import com.orion.ops.task.TaskType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 重新加载发布任务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/14 16:59
 */
@Component
@Order(3100)
@Slf4j
public class AppReleaseTimedTaskRestoreRunner implements CommandLineRunner {

    @Resource
    private ApplicationReleaseDAO applicationReleaseDAO;

    @Resource
    private TaskRegister register;

    @Override
    public void run(String... args) {
        log.info("重新加载发布任务-开始");
        Wrapper<ApplicationReleaseDO> wrapper = new LambdaQueryWrapper<ApplicationReleaseDO>()
                .eq(ApplicationReleaseDO::getReleaseStatus, ReleaseStatus.WAIT_SCHEDULE.getStatus());
        List<ApplicationReleaseDO> releaseList = applicationReleaseDAO.selectList(wrapper);
        for (ApplicationReleaseDO release : releaseList) {
            Long id = release.getId();
            log.info("重新加载发布任务-提交 releaseId: {}", id);
            register.submit(TaskType.RELEASE, release.getTimedReleaseTime(), id);
        }
        log.info("重新加载发布任务-结束");
    }

}
