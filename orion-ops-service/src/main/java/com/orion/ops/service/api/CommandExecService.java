package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.entity.domain.CommandExecDO;
import com.orion.ops.entity.request.CommandExecRequest;
import com.orion.ops.entity.vo.CommandExecVO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 命令执行接口
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/4 17:54
 */
public interface CommandExecService {

    /**
     * 提交命令
     *
     * @param request request
     * @return key: machineId taskId
     */
    HttpWrapper<Map<Long, Long>> batchSubmitTask(CommandExecRequest request);

    /**
     * 命令执行列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<CommandExecVO> execList(@RequestBody CommandExecRequest request);

    /**
     * 命令执行详情
     *
     * @param id id
     * @return detail
     */
    CommandExecVO execDetail(Long id);

    /**
     * 终止任务
     *
     * @param id id
     * @return effect
     */
    Integer terminatedExec(Long id);

    /**
     * 通过id查询 execDO
     *
     * @param id id
     * @return rows
     */
    CommandExecDO selectById(Long id);

}
