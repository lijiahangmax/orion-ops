package com.orion.ops.mapping.template;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.entity.domain.CommandTemplateDO;
import com.orion.ops.entity.vo.template.CommandTemplateVO;

/**
 * 命令模板 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 18:04
 */
public class CommandTemplateConversion {

    static {
        TypeStore.STORE.register(CommandTemplateDO.class, CommandTemplateVO.class, p -> {
            CommandTemplateVO vo = new CommandTemplateVO();
            vo.setId(p.getId());
            vo.setName(p.getTemplateName());
            vo.setValue(p.getTemplateValue());
            vo.setDescription(p.getDescription());
            vo.setCreateUserId(p.getCreateUserId());
            vo.setCreateUserName(p.getCreateUserName());
            vo.setUpdateUserId(p.getUpdateUserId());
            vo.setUpdateUserName(p.getUpdateUserName());
            vo.setCreateTime(p.getCreateTime());
            vo.setUpdateTime(p.getUpdateTime());
            vo.setCreateTimeAgo(Dates.ago(p.getCreateTime()));
            vo.setUpdateTimeAgo(Dates.ago(p.getUpdateTime()));
            return vo;
        });
    }

}
