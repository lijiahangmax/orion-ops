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
package cn.orionsec.ops.service.api;

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.kit.lang.utils.io.Files1;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.entity.domain.MachineSecretKeyDO;
import cn.orionsec.ops.entity.request.machine.MachineKeyRequest;
import cn.orionsec.ops.entity.vo.machine.MachineSecretKeyVO;

import java.util.List;

/**
 * 机器密钥服务
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
     * 查询密钥列表
     *
     * @param request request
     * @return dataGrid
     */
    DataGrid<MachineSecretKeyVO> listKeys(MachineKeyRequest request);

    /**
     * 查询密钥详情
     *
     * @param id id
     * @return row
     */
    MachineSecretKeyVO getKeyDetail(Long id);

    /**
     * 绑定机器密钥
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
