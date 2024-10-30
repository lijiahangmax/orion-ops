/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
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

import cn.orionsec.ops.constant.tail.FileTailType;
import cn.orionsec.ops.entity.request.file.FileTailRequest;
import cn.orionsec.ops.entity.vo.tail.FileTailConfigVO;
import cn.orionsec.ops.entity.vo.tail.FileTailVO;
import com.orion.lang.define.wrapper.DataGrid;

import java.util.List;

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
     * 上传文件
     *
     * @param files files
     */
    void uploadTailFiles(List<FileTailRequest> files);

    /**
     * 删除 tail 文件
     *
     * @param idList idList
     * @return effect
     */
    Integer deleteTailFile(List<Long> idList);

    /**
     * 删除 tail 文件
     *
     * @param machineIdList machineIdList
     * @return effect
     */
    Integer deleteByMachineIdList(List<Long> machineIdList);

    /**
     * tail 列表
     *
     * @param request request
     * @return dataGrid
     */
    DataGrid<FileTailVO> tailFileList(FileTailRequest request);

    /**
     * tail 详情
     *
     * @param id id
     * @return row
     */
    FileTailVO tailFileDetail(Long id);

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

    /**
     * 写入命令
     *
     * @param token   token
     * @param command command
     */
    void writeCommand(String token, String command);

}
