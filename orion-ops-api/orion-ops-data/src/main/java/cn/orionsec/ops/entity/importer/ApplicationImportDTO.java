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
package cn.orionsec.ops.entity.importer;

import cn.orionsec.kit.lang.utils.convert.TypeStore;
import cn.orionsec.kit.office.excel.annotation.ImportField;
import cn.orionsec.ops.entity.domain.ApplicationInfoDO;
import cn.orionsec.ops.entity.vo.data.DataImportCheckRowVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用信息导入
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/6/1 9:43
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "应用信息导入")
public class ApplicationImportDTO extends BaseDataImportDTO {

    @ApiModelProperty(value = "应用名称")
    @ImportField(index = 0)
    private String name;

    @ApiModelProperty(value = "唯一标识")
    @ImportField(index = 1)
    private String tag;

    @ApiModelProperty(value = "应用仓库名称")
    @ImportField(index = 2)
    private String repositoryName;

    @ApiModelProperty(value = "描述")
    @ImportField(index = 3)
    private String description;

    @ApiModelProperty(value = "仓库id", hidden = true)
    private Long repositoryId;

    static {
        TypeStore.STORE.register(ApplicationImportDTO.class, DataImportCheckRowVO.class, p -> {
            DataImportCheckRowVO vo = new DataImportCheckRowVO();
            vo.setSymbol(p.tag);
            vo.setIllegalMessage(p.getIllegalMessage());
            vo.setId(p.getId());
            return vo;
        });
        TypeStore.STORE.register(ApplicationImportDTO.class, ApplicationInfoDO.class, p -> {
            ApplicationInfoDO d = new ApplicationInfoDO();
            d.setId(p.getId());
            d.setAppName(p.name);
            d.setAppTag(p.tag);
            d.setRepoId(p.repositoryId);
            d.setDescription(p.description);
            return d;
        });
    }

}
