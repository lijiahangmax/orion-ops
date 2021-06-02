package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

import java.util.Date;

/**
 * 机器信息
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/14 22:10
 */
@Data
public class MachineInfoVO {

    /**
     * id
     */
    private Long id;

    /**
     * 机房id
     */
    private Long roomId;

    /**
     * 机房名称
     */
    private String roomName;

    /**
     * 代理id
     */
    private Long proxyId;

    /**
     * 代理主机
     */
    private String proxyHost;

    /**
     * 主机ip
     */
    private String host;

    /**
     * ssh端口
     */
    private Integer sshPort;

    /**
     * 机器名称
     */
    private String name;

    /**
     * 机器标签
     */
    private String tag;

    /**
     * 机器描述
     */
    private String description;

    /**
     * 机器账号
     */
    private String username;

    /**
     * 机器key
     */
    private Long keyId;

    /**
     * key名称
     */
    private String keyName;

    /**
     * 机器认证方式 1: 账号认证 2: key认证
     */
    private Integer authType;

    /**
     * 系统类型  1: linux 2: windows
     */
    private Integer systemType;

    /**
     * 机器版本 如: centOS7.0
     */
    private String systemVersion;

    /**
     * 机器状态 1有效 2无效
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    static {
        TypeStore.STORE.register(MachineInfoDO.class, MachineInfoVO.class, p -> {
            MachineInfoVO vo = new MachineInfoVO();
            vo.setId(p.getId());
            vo.setRoomId(p.getRoomId());
            vo.setProxyId(p.getProxyId());
            vo.setHost(p.getMachineHost());
            vo.setSshPort(p.getSshPort());
            vo.setName(p.getUsername());
            vo.setTag(p.getMachineTag());
            vo.setDescription(p.getDescription());
            vo.setUsername(p.getUsername());
            vo.setKeyId(p.getKeyId());
            vo.setAuthType(p.getAuthType());
            vo.setSystemType(p.getSystemType());
            vo.setSystemVersion(p.getSystemVersion());
            vo.setStatus(p.getMachineStatus());
            vo.setCreateTime(p.getCreateTime());
            return vo;
        });
    }

}