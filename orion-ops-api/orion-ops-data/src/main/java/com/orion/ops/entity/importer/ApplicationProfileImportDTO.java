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
package com.orion.ops.entity.importer;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.office.excel.annotation.ImportField;
import com.orion.ops.constant.CnConst;
import com.orion.ops.constant.Const;
import com.orion.ops.entity.domain.ApplicationProfileDO;
import com.orion.ops.entity.vo.data.DataImportCheckRowVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用环境信息导入
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/6/1 9:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "应用环境信息导入")
public class ApplicationProfileImportDTO extends BaseDataImportDTO {

    @ApiModelProperty(value = "环境名称")
    @ImportField(index = 0)
    private String name;

    @ApiModelProperty(value = "唯一标识")
    @ImportField(index = 1)
    private String tag;

    @ApiModelProperty(value = "发布审核")
    @ImportField(index = 2)
    private String releaseAudit;

    @ApiModelProperty(value = "描述")
    @ImportField(index = 3)
    private String description;

    static {
        TypeStore.STORE.register(ApplicationProfileImportDTO.class, DataImportCheckRowVO.class, p -> {
            DataImportCheckRowVO vo = new DataImportCheckRowVO();
            vo.setSymbol(p.tag);
            vo.setIllegalMessage(p.getIllegalMessage());
            vo.setId(p.getId());
            return vo;
        });
        TypeStore.STORE.register(ApplicationProfileImportDTO.class, ApplicationProfileDO.class, p -> {
            ApplicationProfileDO d = new ApplicationProfileDO();
            d.setId(p.getId());
            d.setProfileName(p.name);
            d.setProfileTag(p.tag);
            d.setReleaseAudit(CnConst.OPEN.equals(p.releaseAudit) ? Const.ENABLE : Const.DISABLE);
            d.setDescription(p.description);
            return d;
        });
    }

}
