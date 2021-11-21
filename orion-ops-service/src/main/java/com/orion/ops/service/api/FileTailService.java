package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.tail.FileTailType;
import com.orion.ops.entity.request.FileTailRequest;
import com.orion.ops.entity.vo.FileTailConfigVO;
import com.orion.ops.entity.vo.FileTailVO;

/**
 * 文件 tail service
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/8/1 23:33
 */
public interface FileTailService {

    /**
     * 获取 tail 文件 token 检查文件是否存在
     *
     * @param type  type
     * @param relId relId
     * @return FileTailVO
     */
    FileTailVO getTailToken(FileTailType type, Long relId);

    /**
     * 添加 tail 文件
     *
     * @param request request
     * @return id
     */
    Long insertTailFile(FileTailRequest request);

    /**
     * 修改 tail 文件
     *
     * @param request request
     * @return effect
     */
    Integer updateTailFile(FileTailRequest request);

    /**
     * 删除 tail 文件
     *
     * @param id id
     * @return effect
     */
    Integer deleteTailFile(Long id);

    /**
     * tail 列表
     *
     * @param request request
     * @return dataGrid
     */
    DataGrid<FileTailVO> tailFileList(FileTailRequest request);

    /**
     * 更新 更新时间
     *
     * @param id id
     * @return effect
     */
    Integer updateFileUpdateTime(Long id);

    /**
     * 获取机器配置
     *
     * @param machineId machineId
     * @return config
     */
    FileTailConfigVO getMachineConfig(Long machineId);

}
