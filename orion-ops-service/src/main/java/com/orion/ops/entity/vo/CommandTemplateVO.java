package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.CommandTemplateDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;

/**
 * 命令模板 vo
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/9 18:29
 */
@Data
public class CommandTemplateVO {

    /**
     * id
     */
    private Long id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板类型
     *
     * @see com.orion.ops.consts.TemplateType
     */
    private Integer type;

    /**
     * 模板值
     */
    private String value;

    /**
     * 命令描述
     */
    private String description;

    /**
     * 创建用户id
     */
    private Long createUserId;

    /**
     * 创建用户名
     */
    private String createUserName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建时间
     */
    private String createTimeAgo;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 修改时间
     */
    private String updateTimeAgo;

    static {
        TypeStore.STORE.register(CommandTemplateDO.class, CommandTemplateVO.class, p -> {
            CommandTemplateVO vo = new CommandTemplateVO();
            vo.setId(p.getId());
            vo.setName(p.getTemplateName());
            vo.setType(p.getTemplateType());
            vo.setValue(p.getTemplateValue());
            vo.setDescription(p.getDescription());
            vo.setCreateUserId(p.getCreateUserId());
            vo.setCreateUserName(p.getCreateUserName());
            vo.setCreateTime(p.getCreateTime());
            vo.setUpdateTime(p.getUpdateTime());
            vo.setCreateTimeAgo(Dates.ago(p.getCreateTime()));
            vo.setUpdateTimeAgo(Dates.ago(p.getUpdateTime()));
            return vo;
        });
    }

}
