package com.orion.ops.entity.vo.sftp;

import com.orion.net.base.file.sftp.SftpFile;
import com.orion.ops.consts.Const;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.io.FileType;
import com.orion.utils.io.Files1;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

/**
 * sftp 文件详情响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 18:36
 */
@Data
@ApiModel(value = "文件详情响应")
public class FileDetailVO {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "绝对路径")
    private String path;

    @ApiModelProperty(value = "文件大小")
    private String size;

    @ApiModelProperty(value = "文件大小(byte)")
    private Long sizeByte;

    @ApiModelProperty(value = "属性")
    private String attr;

    @ApiModelProperty(value = "10进制表现的8进制权限")
    private Integer permission;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "组id")
    private Integer gid;

    @ApiModelProperty(value = "更新时间")
    private Date modifyTime;

    @ApiModelProperty(value = "是否为目录")
    private Boolean isDir;

    @ApiModelProperty(value = "是否安全")
    private Boolean isSafe;

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

}
