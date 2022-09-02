package com.orion.ops.mapping.sftp;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.io.FileType;
import com.orion.lang.utils.io.Files1;
import com.orion.net.base.file.sftp.SftpFile;
import com.orion.ops.constant.Const;
import com.orion.ops.entity.dto.sftp.SftpUploadInfoDTO;
import com.orion.ops.entity.request.sftp.FileUploadRequest;
import com.orion.ops.entity.vo.sftp.FileDetailVO;

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
