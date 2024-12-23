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
package cn.orionsec.ops.mapping.upload;

import cn.orionsec.kit.lang.utils.convert.TypeStore;
import cn.orionsec.ops.entity.domain.MachineInfoDO;
import cn.orionsec.ops.entity.dto.sftp.SftpUploadInfoDTO;
import cn.orionsec.ops.entity.request.upload.BatchUploadRequest;
import cn.orionsec.ops.entity.vo.upload.BatchUploadCheckFileVO;
import cn.orionsec.ops.entity.vo.upload.BatchUploadCheckMachineVO;

/**
 * 文件批量上传 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/13 17:04
 */
public class BatchUploadConversion {

    static {
        TypeStore.STORE.register(BatchUploadRequest.class, SftpUploadInfoDTO.class, p -> {
            SftpUploadInfoDTO dto = new SftpUploadInfoDTO();
            dto.setRemotePath(p.getRemotePath());
            dto.setMachineIdList(p.getMachineIds());
            return dto;
        });
    }

    static {
        TypeStore.STORE.register(MachineInfoDO.class, BatchUploadCheckFileVO.class, p -> {
            BatchUploadCheckFileVO vo = new BatchUploadCheckFileVO();
            vo.setId(p.getId());
            vo.setName(p.getMachineName());
            vo.setHost(p.getMachineHost());
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(MachineInfoDO.class, BatchUploadCheckMachineVO.class, p -> {
            BatchUploadCheckMachineVO vo = new BatchUploadCheckMachineVO();
            vo.setId(p.getId());
            vo.setName(p.getMachineName());
            vo.setHost(p.getMachineHost());
            return vo;
        });
    }

}
