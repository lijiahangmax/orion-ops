package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.dao.ApplicationPipelineTaskLogDAO;
import com.orion.ops.entity.domain.ApplicationPipelineTaskLogDO;
import com.orion.ops.entity.vo.ApplicationPipelineTaskLogVO;
import com.orion.ops.service.api.ApplicationPipelineTaskLogService;
import com.orion.ops.utils.DataQuery;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 应用流水线任务日志服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/24 20:53
 */
@Service("applicationPipelineTaskLogService")
public class ApplicationPipelineTaskLogServiceImpl implements ApplicationPipelineTaskLogService {

    @Resource
    private ApplicationPipelineTaskLogDAO applicationPipelineTaskLogDAO;

    @Override
    public List<ApplicationPipelineTaskLogVO> getLogList(Long taskId) {
        LambdaQueryWrapper<ApplicationPipelineTaskLogDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineTaskLogDO>()
                .eq(ApplicationPipelineTaskLogDO::getTaskId, taskId);
        return DataQuery.of(applicationPipelineTaskLogDAO)
                .wrapper(wrapper)
                .list(ApplicationPipelineTaskLogVO.class);
    }

    @Override
    public Integer deleteByTaskIdList(List<Long> taskIdList) {
        Wrapper<ApplicationPipelineTaskLogDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineTaskLogDO>()
                .in(ApplicationPipelineTaskLogDO::getTaskId, taskIdList);
        return applicationPipelineTaskLogDAO.delete(wrapper);
    }

}
