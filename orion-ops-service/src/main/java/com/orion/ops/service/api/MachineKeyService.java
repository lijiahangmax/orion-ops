package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.machine.MachineEnvAttr;
import com.orion.ops.consts.machine.MountKeyStatus;
import com.orion.ops.entity.domain.MachineSecretKeyDO;
import com.orion.ops.entity.request.MachineKeyRequest;
import com.orion.ops.entity.vo.MachineSecretKeyVO;
import com.orion.utils.io.Files1;

import java.util.List;

/**
 * 机器秘钥service
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
     * 删除key
     *
     * @param idList idList
     * @return effect
     */
    Integer removeSecretKey(List<Long> idList);

    /**
     * 通过id查询key
     *
     * @param id id
     * @return key
     */
    MachineSecretKeyDO getKeyById(Long id);

    /**
     * 查询keys
     *
     * @param request request
     * @return dataGrid
     */
    DataGrid<MachineSecretKeyVO> listKeys(MachineKeyRequest request);

    /**
     * 挂载秘钥
     *
     * @param id id
     * @return status
     * @see MountKeyStatus
     */
    Integer mountKey(Long id);

    /**
     * 卸载秘钥
     *
     * @param id id
     * @return status
     * @see MountKeyStatus
     */
    Integer unmountKey(Long id);

    /**
     * 挂载秘钥
     *
     * @param fileData fileData
     * @param password password
     * @return status
     * @see MountKeyStatus
     */
    Integer tempMountKey(String fileData, String password);

    /**
     * 获取key的实际路径
     *
     * @param path path
     * @return 实际路径
     */
    static String getKeyPath(String path) {
        return Files1.getPath(MachineEnvAttr.KEY_PATH.getValue() + path);
    }

}
