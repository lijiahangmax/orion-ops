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
package com.orion.ops.entity.exporter;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.office.excel.annotation.ExportField;
import com.orion.office.excel.annotation.ExportSheet;
import com.orion.office.excel.annotation.ExportTitle;
import com.orion.ops.entity.domain.FileTailListDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 日志文件导出
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/30 10:40
 */
@Data
@ApiModel(value = "日志文件导出")
@ExportTitle(title = "日志文件导出")
@ExportSheet(name = "日志文件", titleHeight = 22, headerHeight = 22, freezeHeader = true, filterHeader = true)
public class MachineTailFileExportDTO {

    @ApiModelProperty(value = "机器名称")
    @ExportField(index = 0, header = "机器名称", width = 20, wrapText = true)
    private String machineName;

    @ApiModelProperty(value = "机器标识")
    @ExportField(index = 1, header = "机器标识", width = 20, wrapText = true)
    private String machineTag;

    @ApiModelProperty(value = "文件别名")
    @ExportField(index = 2, header = "文件别名", width = 20, wrapText = true)
    private String name;

    @ApiModelProperty(value = "文件路径")
    @ExportField(index = 3, header = "文件路径", width = 50, wrapText = true)
    private String path;

    @ApiModelProperty(value = "文件编码")
    @ExportField(index = 4, header = "文件编码", width = 12)
    private String charset;

    @ApiModelProperty(value = "尾部偏移行")
    @ExportField(index = 5, header = "尾部偏移行", width = 12)
    private Integer offset;

    @ApiModelProperty(value = "执行命令")
    @ExportField(index = 6, header = "执行命令", width = 35, wrapText = true)
    private String command;

    @ApiModelProperty(value = "机器id", hidden = true)
    private Long machineId;

    static {
        TypeStore.STORE.register(FileTailListDO.class, MachineTailFileExportDTO.class, p -> {
            MachineTailFileExportDTO dto = new MachineTailFileExportDTO();
            dto.setMachineId(p.getMachineId());
            dto.setName(p.getAliasName());
            dto.setPath(p.getFilePath());
            dto.setCharset(p.getFileCharset());
            dto.setOffset(p.getFileOffset());
            dto.setCommand(p.getTailCommand());
            return dto;
        });
    }

}
