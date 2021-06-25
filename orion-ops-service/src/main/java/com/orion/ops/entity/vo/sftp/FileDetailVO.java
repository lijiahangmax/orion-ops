package com.orion.ops.entity.vo.sftp;

import com.orion.remote.channel.sftp.SftpFile;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.io.Files1;
import lombok.Data;

import java.util.Date;

/**
 * sftp ls 文件信息
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 18:36
 */
@Data
public class FileDetailVO {

    /**
     * 名称
     */
    private String name;

    /**
     * 绝对路径
     */
    private String path;

    /**
     * 大小
     */
    private String size;

    /**
     * 属性
     */
    private String attr;

    /**
     * 10进制表现的8进制权限
     */
    private Integer permission;

    /**
     * 用户id
     */
    private Integer uid;

    /**
     * 组id
     */
    private Integer gid;

    /**
     * 更新时间
     */
    private Date modifyTime;

    static {
        TypeStore.STORE.register(SftpFile.class, FileDetailVO.class, s -> {
            FileDetailVO vo = new FileDetailVO();
            vo.setName(s.getName());
            vo.setPath(s.getPath());
            vo.setSize(Files1.getSize(s.getSize()));
            vo.setPermission(s.getPermission());
            vo.setUid(s.getUid());
            vo.setGid(s.getGid());
            vo.setAttr(s.getPermissionString());
            vo.setModifyTime(s.getModifyTime());
            return vo;
        });
    }

}
