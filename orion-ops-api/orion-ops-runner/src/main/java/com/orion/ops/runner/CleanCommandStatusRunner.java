package com.orion.ops.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.constant.command.ExecStatus;
import com.orion.ops.dao.CommandExecDAO;
import com.orion.ops.entity.domain.CommandExecDO;
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
public class CleanCommandStatusRunner implements CommandLineRunner {

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
