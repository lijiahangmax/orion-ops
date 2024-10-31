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
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.tail.FileTailMode;
import cn.orionsec.ops.entity.domain.FileTailListDO;
import cn.orionsec.ops.entity.vo.data.DataImportCheckRowVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 机器代理导入
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/31 9:54
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "机器代理导入")
public class MachineTailFileImportDTO extends BaseDataImportDTO {

    @ApiModelProperty(value = "机器标识")
    @ImportField(index = 1)
    private String machineTag;

    @ApiModelProperty(value = "别名")
    @ImportField(index = 2)
    private String name;

    @ApiModelProperty(value = "文件路径")
    @ImportField(index = 3)
    private String path;

    @ApiModelProperty(value = "文件编码")
    @ImportField(index = 4)
    private String charset;

    @ApiModelProperty(value = "尾部偏移行")
    @ImportField(index = 5)
    private Integer offset;

    @ApiModelProperty(value = "执行命令")
    @ImportField(index = 6)
    private String command;

    @ApiModelProperty(value = "机器id", hidden = true)
    private Long machineId;

    static {
        TypeStore.STORE.register(MachineTailFileImportDTO.class, DataImportCheckRowVO.class, p -> {
            DataImportCheckRowVO vo = new DataImportCheckRowVO();
            vo.setSymbol(p.name);
            vo.setIllegalMessage(p.getIllegalMessage());
            vo.setId(p.getId());
            return vo;
        });
        TypeStore.STORE.register(MachineTailFileImportDTO.class, FileTailListDO.class, p -> {
            FileTailListDO d = new FileTailListDO();
            d.setId(p.getId());
            d.setMachineId(p.machineId);
            d.setAliasName(p.name);
            d.setFilePath(p.path);
            d.setFileCharset(p.charset);
            d.setFileOffset(p.offset);
            d.setTailCommand(p.command);
            if (Const.HOST_MACHINE_ID.equals(p.machineId)) {
                d.setTailMode(FileTailMode.TRACKER.getMode());
            } else {
                d.setTailMode(FileTailMode.TAIL.getMode());
            }
            return d;
        });
    }

}
