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
package cn.orionsec.ops.mapping.sftp;

import cn.orionsec.kit.lang.utils.convert.TypeStore;
import cn.orionsec.kit.lang.utils.io.FileType;
import cn.orionsec.kit.lang.utils.io.Files1;
import cn.orionsec.kit.net.host.sftp.SftpFile;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.entity.dto.sftp.SftpUploadInfoDTO;
import cn.orionsec.ops.entity.request.sftp.FileUploadRequest;
import cn.orionsec.ops.entity.vo.sftp.FileDetailVO;

import java.util.Optional;

/**
 * sftp 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 16:51
 */
public class SftpConversion {

    static {
        TypeStore.STORE.register(SftpFile.class, FileDetailVO.class, s -> {
            FileDetailVO vo = new FileDetailVO();
            vo.setName(s.getName());
            vo.setPath(s.getPath());
            vo.setSize(Files1.getSize(s.getSize()));
            vo.setSizeByte(s.getSize());
            vo.setPermission(s.getPermission());
            vo.setUid(s.getUid());
            vo.setGid(s.getGid());
            vo.setAttr(s.getPermissionString());
            vo.setModifyTime(s.getModifyTime());
            Boolean isDir = Optional.ofNullable(FileType.of(vo.getAttr()))
                    .map(FileType.DIRECTORY::equals)
                    .orElse(false);
            vo.setIsDir(isDir);
            vo.setIsSafe(!Const.UNSAFE_FS_DIR.contains(s.getPath()));
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(FileUploadRequest.class, SftpUploadInfoDTO.class, p -> {
            SftpUploadInfoDTO dto = new SftpUploadInfoDTO();
            dto.setRemotePath(p.getRemotePath());
            return dto;
        });
    }

}
