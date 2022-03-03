package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.consts.app.ActionStatus;
import com.orion.ops.consts.app.StageType;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.dao.ApplicationActionLogDAO;
import com.orion.ops.entity.domain.ApplicationActionLogDO;
import com.orion.ops.entity.vo.ApplicationActionLogVO;
import com.orion.ops.service.api.ApplicationActionLogService;
import com.orion.ops.utils.DataQuery;
import com.orion.utils.Strings;
import com.orion.utils.io.Files1;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 应用操作日志服务实现
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/11 16:41
 */
@Service("applicationActionLogService")
public class ApplicationActionLogServiceImpl implements ApplicationActionLogService {

    @Resource
    private ApplicationActionLogDAO applicationActionLogDAO;

    @Override
    public List<ApplicationActionLogVO> getActionLogsByRelId(Long relId, StageType stageType) {
        LambdaQueryWrapper<ApplicationActionLogDO> wrapper = new LambdaQueryWrapper<ApplicationActionLogDO>()
                .eq(ApplicationActionLogDO::getRelId, relId)
                .eq(ApplicationActionLogDO::getStageType, stageType.getType())
                .orderByAsc(ApplicationActionLogDO::getId);
        return DataQuery.of(applicationActionLogDAO)
                .wrapper(wrapper)
                .list(ApplicationActionLogVO.class);
    }

    @Override
    public Integer deleteByRelId(Long relId, StageType stageType) {
        LambdaQueryWrapper<ApplicationActionLogDO> wrapper = new LambdaQueryWrapper<ApplicationActionLogDO>()
                .eq(ApplicationActionLogDO::getRelId, relId)
                .eq(ApplicationActionLogDO::getStageType, stageType.getType());
        return applicationActionLogDAO.delete(wrapper);
    }

    @Override
    public List<ApplicationActionLogDO> selectActionByRelId(Long relId, StageType stageType) {
        LambdaQueryWrapper<ApplicationActionLogDO> wrapper = new LambdaQueryWrapper<ApplicationActionLogDO>()
                .eq(ApplicationActionLogDO::getRelId, relId)
                .eq(ApplicationActionLogDO::getStageType, stageType.getType())
                .orderByAsc(ApplicationActionLogDO::getId);
        return applicationActionLogDAO.selectList(wrapper);
    }

    @Override
    public List<ApplicationActionLogDO> selectActionByRelIdList(List<Long> relIdList, StageType stageType) {
        LambdaQueryWrapper<ApplicationActionLogDO> wrapper = new LambdaQueryWrapper<ApplicationActionLogDO>()
                .in(ApplicationActionLogDO::getRelId, relIdList)
                .eq(ApplicationActionLogDO::getStageType, stageType.getType())
                .orderByAsc(ApplicationActionLogDO::getId);
        return applicationActionLogDAO.selectList(wrapper);
    }

    @Override
    public void updateActionById(ApplicationActionLogDO record) {
        if (record.getUpdateTime() == null) {
            record.setUpdateTime(new Date());
        }
        applicationActionLogDAO.updateById(record);
    }

    @Override
    public String getActionLogPath(Long id) {
        return Optional.ofNullable(applicationActionLogDAO.selectById(id))
                .map(ApplicationActionLogDO::getLogPath)
                .filter(Strings::isNotBlank)
                .map(s -> Files1.getPath(SystemEnvAttr.LOG_PATH.getValue(), s))
                .orElse(null);
    }

    @Override
    public void resetActionStatus(Long relId, StageType stageType) {
        // 查询action
        List<ApplicationActionLogDO> actions = this.selectActionByRelId(relId, stageType);
        // 修改状态
        for (ApplicationActionLogDO action : actions) {
            ApplicationActionLogDO update = new ApplicationActionLogDO();
            update.setId(action.getId());
            update.setUpdateTime(new Date());
            switch (ActionStatus.of(action.getRunStatus())) {
                case WAIT:
                case RUNNABLE:
                    update.setRunStatus(ActionStatus.TERMINATED.getStatus());
                    break;
                default:
                    break;
            }
            applicationActionLogDAO.updateById(update);
        }
    }

}
