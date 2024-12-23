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
package cn.orionsec.ops.handler.sftp.hint;

import cn.orionsec.ops.constant.sftp.SftpTransferType;
import cn.orionsec.ops.entity.domain.FileTransferLogDO;
import lombok.Data;

import java.util.List;

/**
 * 文件传输配置
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/17 17:48
 */
@Data
public class FileTransferHint {

    /**
     * record
     */
    private FileTransferLogDO record;

    /**
     * 类型
     */
    private SftpTransferType type;

    public FileTransferHint(FileTransferLogDO record) {
        this.record = record;
        this.type = SftpTransferType.of(record.getTransferType());
    }

    /**
     * 获取上传配置
     *
     * @param record record
     * @return 上传配置
     */
    public static FileTransferHint transfer(FileTransferLogDO record) {
        return new FileTransferHint(record);
    }

    /**
     * 获取打包配置
     *
     * @param record   record
     * @param fileList fileList
     * @return 上传配置
     */
    public static FilePackageHint packaged(FileTransferLogDO record, List<FileTransferLogDO> fileList) {
        return new FilePackageHint(record, fileList);
    }

}
