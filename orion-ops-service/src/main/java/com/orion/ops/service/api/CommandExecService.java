package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.entity.domain.CommandExecDO;
import com.orion.ops.entity.request.CommandExecRequest;
import com.orion.ops.entity.vo.CommandExecStatusVO;
import com.orion.ops.entity.vo.CommandExecVO;
import com.orion.ops.entity.vo.sftp.CommandTaskSubmitVO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

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
     * @return tasks
     */
    List<CommandTaskSubmitVO> batchSubmitTask(CommandExecRequest request);

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
     * 写入命令
     *
     * @param id      id
     * @param command command
     */
    void writeCommand(Long id, String command);

    /**
     * 终止任务
     *
     * @param id id
     */
    void terminatedExec(Long id);

    /**
     * 删除任务
     *
     * @param idList idList
     * @return effect
     */
    Integer deleteTask(List<Long> idList);

    /**
     * 查询状态
     *
     * @param execIdList execIdList
     * @return status
     */
    List<CommandExecStatusVO> getExecStatusList(List<Long> execIdList);

    /**
     * 通过id查询 execDO
     *
     * @param id id
     * @return rows
     */
    CommandExecDO selectById(Long id);

    /**
     * 获取 exec command 日志
     *
     * @param id id
     * @return logPath
     */
    String getExecLogFilePath(Long id);

}
