package com.orion.ops.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.consts.app.ReleaseStatus;
import com.orion.ops.dao.ReleaseActionDAO;
import com.orion.ops.dao.ReleaseBillDAO;
import com.orion.ops.dao.ReleaseMachineDAO;
import com.orion.ops.entity.domain.ReleaseActionDO;
import com.orion.ops.entity.domain.ReleaseBillDO;
import com.orion.ops.entity.domain.ReleaseMachineDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 重置上线单状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/29 17:49
 */
@Component
@Order(150)
@Slf4j
public class CleanReleaseStatusRunner implements CommandLineRunner {

    @Resource
    private ReleaseBillDAO releaseBillDAO;

    @Resource
    private ReleaseMachineDAO releaseMachineDAO;

    @Resource
    private ReleaseActionDAO releaseActionDAO;

    @Override
    public void run(String... args) {
        log.info("重置上线单状态-开始");
        LambdaQueryWrapper<ReleaseBillDO> releaseBillWrapper = new LambdaQueryWrapper<ReleaseBillDO>()
                .eq(ReleaseBillDO::getReleaseStatus, ReleaseStatus.RUNNABLE.getStatus());
        List<ReleaseBillDO> releaseBills = releaseBillDAO.selectList(releaseBillWrapper);
        for (ReleaseBillDO releaseBill : releaseBills) {
            // 状态改为异常
            Long id = releaseBill.getId();
            ReleaseBillDO update = new ReleaseBillDO();
            update.setId(id);
            update.setReleaseStatus(ReleaseStatus.EXCEPTION.getStatus());
            update.setReleaseEndTime(new Date());
            releaseBillDAO.updateById(update);
            // 更新机器
            this.updateMachine(id);
            // 更新action
            this.updateAction(id);
            log.info("重置上线单状态-重置 {}", id);
        }
        log.info("重置上线单状态-结束");
    }

    /**
     * 更新机器状态
     *
     * @param releaseId releaseId
     */
    private void updateMachine(Long releaseId) {
        LambdaQueryWrapper<ReleaseMachineDO> wrapper = new LambdaQueryWrapper<ReleaseMachineDO>()
                .eq(ReleaseMachineDO::getReleaseId, releaseId);
        // 查询列表
        List<ReleaseMachineDO> machines = releaseMachineDAO.selectList(wrapper);
        for (ReleaseMachineDO machine : machines) {
            ActionStatus status = ActionStatus.of(machine.getRunStatus());
            ReleaseMachineDO update = new ReleaseMachineDO();
            update.setId(machine.getId());
            if (ActionStatus.WAIT_RUNNABLE.equals(status)) {
                // 未开始改为跳过
                update.setRunStatus(ActionStatus.SKIPPED.getStatus());
                releaseMachineDAO.updateById(update);
            } else if (ActionStatus.RUNNABLE.equals(status)) {
                // 执行中改为异常
                update.setRunStatus(ActionStatus.EXCEPTION.getStatus());
                update.setEndTime(new Date());
                releaseMachineDAO.updateById(update);
            }
        }
    }

    /**
     * 更新action
     *
     * @param releaseId releaseId
     */
    private void updateAction(Long releaseId) {
        LambdaQueryWrapper<ReleaseActionDO> wrapper = new LambdaQueryWrapper<ReleaseActionDO>()
                .eq(ReleaseActionDO::getReleaseId, releaseId);
        // 查询列表
        List<ReleaseActionDO> actions = releaseActionDAO.selectList(wrapper);
        for (ReleaseActionDO action : actions) {
            ActionStatus status = ActionStatus.of(action.getRunStatus());
            ReleaseActionDO update = new ReleaseActionDO();
            update.setId(action.getId());
            if (ActionStatus.WAIT_RUNNABLE.equals(status)) {
                // 未开始改为跳过
                update.setRunStatus(ActionStatus.SKIPPED.getStatus());
                releaseActionDAO.updateById(update);
            } else if (ActionStatus.RUNNABLE.equals(status)) {
                // 执行中改为异常
                update.setRunStatus(ActionStatus.EXCEPTION.getStatus());
                update.setEndTime(new Date());
                releaseActionDAO.updateById(update);
            }
        }
    }

}
