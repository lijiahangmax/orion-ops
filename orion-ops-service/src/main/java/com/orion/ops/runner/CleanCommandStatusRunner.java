package com.orion.ops.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.command.ExecStatus;
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
@Order(130)
@Slf4j
public class CleanCommandStatusRunner implements CommandLineRunner {

    @Resource
    private CommandExecDAO commandExecDAO;

    @Override
    public void run(String... args) {
        log.info("重置命令执行状态-开始");
        LambdaQueryWrapper<CommandExecDO> wrapper = new LambdaQueryWrapper<CommandExecDO>()
                .in(CommandExecDO::getExecStatus, ExecStatus.WAITING.getStatus(), ExecStatus.RUNNABLE.getStatus());
        commandExecDAO.selectList(wrapper).forEach(c -> {
            CommandExecDO update = new CommandExecDO();
            update.setId(c.getId());
            update.setEndDate(new Date());
            update.setExecStatus(Const.TERMINATED_EXIT_CODE);
            update.setExecStatus(ExecStatus.TERMINATED.getStatus());
            commandExecDAO.updateById(update);
            log.info("重置命令执行状态-重置 {}", c.getId());
        });
        log.info("重置命令执行状态-结束");
    }

}
