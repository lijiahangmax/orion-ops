package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationVcsDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

import java.util.Date;

/**
 * 应用版本控制信息
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/28 13:47
 */
@Data
public class ApplicationVcsVO {

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 类型 1git
     *
     * @see com.orion.ops.consts.app.VcsType
     */
    private Integer type;

    /**
     * url
     */
    private String url;

    /**
     * 用户名
     */
    private String username;

    /**
     * token
     */
    private String token;

    /**
     * 状态 10未初始化 20初始化中 30正常 40失败
     *
     * @see com.orion.ops.consts.app.VcsStatus
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    static {
        TypeStore.STORE.register(ApplicationVcsDO.class, ApplicationVcsVO.class, p -> {
            ApplicationVcsVO vo = new ApplicationVcsVO();
            vo.setId(p.getId());
            vo.setName(p.getVcsName());
            vo.setDescription(p.getVcsDescription());
            vo.setType(p.getVcsType());
            vo.setUrl(p.getVscUrl());
            vo.setUsername(p.getVscUsername());
            vo.setToken(p.getVcsAccessToken());
            vo.setStatus(p.getVcsStatus());
            vo.setCreateTime(p.getCreateTime());
            vo.setUpdateTime(p.getUpdateTime());
            return vo;
        });
    }

}
