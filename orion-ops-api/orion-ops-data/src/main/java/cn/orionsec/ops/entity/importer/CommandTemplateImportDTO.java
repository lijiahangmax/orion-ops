/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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
package cn.orionsec.ops.entity.importer;

import cn.orionsec.kit.lang.utils.convert.TypeStore;
import cn.orionsec.kit.office.excel.annotation.ImportField;
import cn.orionsec.ops.entity.domain.CommandTemplateDO;
import cn.orionsec.ops.entity.vo.data.DataImportCheckRowVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 命令模板导入
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/6/1 9:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "命令模板导入")
public class CommandTemplateImportDTO extends BaseDataImportDTO {

    @ApiModelProperty(value = "模板名称")
    @ImportField(index = 0)
    private String name;

    @ApiModelProperty(value = "模板命令")
    @ImportField(index = 1)
    private String template;

    @ApiModelProperty(value = "描述")
    @ImportField(index = 2)
    private String description;

    static {
        TypeStore.STORE.register(CommandTemplateImportDTO.class, DataImportCheckRowVO.class, p -> {
            DataImportCheckRowVO vo = new DataImportCheckRowVO();
            vo.setSymbol(p.name);
            vo.setIllegalMessage(p.getIllegalMessage());
            vo.setId(p.getId());
            return vo;
        });
        TypeStore.STORE.register(CommandTemplateImportDTO.class, CommandTemplateDO.class, p -> {
            CommandTemplateDO d = new CommandTemplateDO();
            d.setId(p.getId());
            d.setTemplateName(p.name);
            d.setTemplateValue(p.template);
            d.setDescription(p.description);
            return d;
        });
    }

}
