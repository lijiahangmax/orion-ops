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

import cn.orionsec.ops.constant.command.ExecStatus;
import cn.orionsec.ops.dao.CommandExecDAO;
import cn.orionsec.ops.entity.domain.CommandExecDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 重置执行任务状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/5 0:18
 */
@Component
@Order(2200)
@Slf4j
public class CommandExecStatusCleanRunner implements CommandLineRunner {

    @Resource
    private CommandExecDAO commandExecDAO;

    @Override
    public void run(String... args) {
        log.info("重置命令执行状态-开始");
        LambdaQueryWrapper<CommandExecDO> wrapper = new LambdaQueryWrapper<CommandExecDO>()
                .in(CommandExecDO::getExecStatus, ExecStatus.WAITING.getStatus(), ExecStatus.RUNNABLE.getStatus());
        // 更新执行状态
        CommandExecDO update = new CommandExecDO();
        update.setExecStatus(ExecStatus.TERMINATED.getStatus());
        update.setUpdateTime(new Date());
        commandExecDAO.update(update, wrapper);
        log.info("重置命令执行状态-结束");
    }

}
