package com.orion.ops.entity.vo;

import lombok.Data;

import java.util.Date;

/**
 * 机房
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/14 20:32
 */
@Data
public class MachineRoomVO {

    /**
     * 机房id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 标识
     */
    private String tag;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态 1正常 2关闭
     */
    private Integer status;

    /**
     * 负责人id
     */
    private Long managerId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

}
