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
package cn.orionsec.ops.service.impl;

import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.convert.Converts;
import cn.orionsec.kit.lang.utils.io.Files1;
import cn.orionsec.ops.constant.app.ActionStatus;
import cn.orionsec.ops.constant.app.StageType;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.dao.ApplicationActionLogDAO;
import cn.orionsec.ops.entity.domain.ApplicationActionLogDO;
import cn.orionsec.ops.entity.vo.app.ApplicationActionLogVO;
import cn.orionsec.ops.service.api.ApplicationActionLogService;
import cn.orionsec.ops.utils.DataQuery;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    public ApplicationActionLogVO getDetailById(Long id) {
        ApplicationActionLogDO log = applicationActionLogDAO.selectById(id);
        return Converts.to(log, ApplicationActionLogVO.class);
    }

    @Override
    public ApplicationActionLogVO getStatusById(Long id) {
        ApplicationActionLogDO log = applicationActionLogDAO.selectStatusInfoById(id);
        return Converts.to(log, ApplicationActionLogVO.class);
    }

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
    public Integer deleteByRelIdList(List<Long> relIdList, StageType stageType) {
        LambdaQueryWrapper<ApplicationActionLogDO> wrapper = new LambdaQueryWrapper<ApplicationActionLogDO>()
                .in(ApplicationActionLogDO::getRelId, relIdList)
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
