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
package cn.orionsec.ops.runner;

import cn.orionsec.ops.constant.app.ReleaseStatus;
import cn.orionsec.ops.dao.ApplicationReleaseDAO;
import cn.orionsec.ops.entity.domain.ApplicationReleaseDO;
import cn.orionsec.ops.task.TaskRegister;
import cn.orionsec.ops.task.TaskType;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
