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
package cn.orionsec.ops.mapping.template;

import cn.orionsec.kit.lang.utils.convert.TypeStore;
import cn.orionsec.kit.lang.utils.time.Dates;
import cn.orionsec.ops.entity.domain.CommandTemplateDO;
import cn.orionsec.ops.entity.vo.template.CommandTemplateVO;

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
