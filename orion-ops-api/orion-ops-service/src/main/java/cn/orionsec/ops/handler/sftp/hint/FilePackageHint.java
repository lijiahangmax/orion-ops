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

import cn.orionsec.ops.entity.domain.FileTransferLogDO;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 文件打包参数
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/17 17:53
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class FilePackageHint extends FileTransferHint {

    /**
     * 压缩文件列表
     */
    private List<FileTransferLogDO> fileList;

    public FilePackageHint(FileTransferLogDO record, List<FileTransferLogDO> fileList) {
        super(record);
        this.fileList = fileList;
    }

}
