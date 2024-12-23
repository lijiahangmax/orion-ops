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
package cn.orionsec.ops.mapping.file;

import cn.orionsec.kit.lang.utils.convert.TypeStore;
import cn.orionsec.kit.lang.utils.io.Files1;
import cn.orionsec.ops.entity.domain.FileTransferLogDO;
import cn.orionsec.ops.entity.vo.sftp.FileTransferLogVO;

/**
 * 文件传输日志 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 18:07
 */
public class FileTransferLogConversion {

    static {
        TypeStore.STORE.register(FileTransferLogDO.class, FileTransferLogVO.class, p -> {
            FileTransferLogVO vo = new FileTransferLogVO();
            vo.setId(p.getId());
            vo.setMachineId(p.getMachineId());
            vo.setFileToken(p.getFileToken());
            vo.setType(p.getTransferType());
            vo.setRemoteFile(p.getRemoteFile());
            vo.setCurrent(Files1.getSize(p.getCurrentSize()));
            vo.setSize(Files1.getSize(p.getFileSize()));
            vo.setProgress(p.getNowProgress());
            vo.setStatus(p.getTransferStatus());
            return vo;
        });
    }

}
