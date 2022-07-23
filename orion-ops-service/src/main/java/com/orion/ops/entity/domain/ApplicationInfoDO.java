package com.orion.ops.entity.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 应用表
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-07-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("application_info")
public class ApplicationInfoDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    @TableField("app_name")
    private String appName;

    /**
     * 应用唯一标识
     */
    @TableField("app_tag")
    private String appTag;

    /**
     * 排序
     */
    @TableField("app_sort")
    private Integer appSort;

    /**
     * 应用版本仓库id
     */
    @TableField(value = "repo_id", updateStrategy = FieldStrategy.IGNORED)
    private Long repoId;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 是否删除 1未删除 2已删除
     *
     * @see com.orion.ops.constant.Const#NOT_DELETED
     * @see com.orion.ops.constant.Const#IS_DELETED
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Date updateTime;

}
