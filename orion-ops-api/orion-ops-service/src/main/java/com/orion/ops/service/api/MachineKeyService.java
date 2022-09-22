package com.orion.ops.service.api;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.utils.io.Files1;
import com.orion.ops.constant.system.SystemEnvAttr;
import com.orion.ops.entity.domain.MachineSecretKeyDO;
import com.orion.ops.entity.request.machine.MachineKeyRequest;
import com.orion.ops.entity.vo.machine.MachineSecretKeyVO;

import java.util.List;

/**
 * 机器秘钥服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/5 11:03
 */
public interface MachineKeyService {

    /**
     * 添加 key
     *
     * @param request request
     * @return id
     */
    Long addSecretKey(MachineKeyRequest request);

    /**
     * 修改 key
     *
     * @param request request
     * @return effect
     */
    Integer updateSecretKey(MachineKeyRequest request);

    /**
     * 删除 key
     *
     * @param idList idList
     * @return effect
     */
    Integer removeSecretKey(List<Long> idList);

    /**
     * 通过 id 查询 key
     *
     * @param id id
     * @return key
     */
    MachineSecretKeyDO getKeyById(Long id);

    /**
     * 查询秘钥列表
     *
     * @param request request
     * @return dataGrid
     */
    DataGrid<MachineSecretKeyVO> listKeys(MachineKeyRequest request);

    /**
     * 查询秘钥详情
     *
     * @param id id
     * @return row
     */
    MachineSecretKeyVO getKeyDetail(Long id);

    /**
     * 绑定机器秘钥
     *
     * @param id            id
     * @param machineIdList 机器id
     */
    void bindMachineKey(Long id, List<Long> machineIdList);

    /**
     * 获取key的实际路径
     *
     * @param path path
     * @return 实际路径
     */
    static String getKeyPath(String path) {
        return Files1.getPath(SystemEnvAttr.KEY_PATH.getValue(), path);
    }

}
