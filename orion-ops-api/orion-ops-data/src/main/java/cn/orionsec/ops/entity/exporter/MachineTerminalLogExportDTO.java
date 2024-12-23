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
package cn.orionsec.ops.entity.exporter;

import cn.orionsec.kit.lang.utils.convert.TypeStore;
import cn.orionsec.kit.lang.utils.time.Dates;
import cn.orionsec.kit.office.excel.annotation.ExportField;
import cn.orionsec.kit.office.excel.annotation.ExportSheet;
import cn.orionsec.kit.office.excel.annotation.ExportTitle;
import cn.orionsec.kit.office.excel.type.ExcelFieldType;
import cn.orionsec.ops.entity.domain.MachineTerminalLogDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 终端日志导出
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/30 10:27
 */
@Data
@ApiModel(value = "终端日志导出")
@ExportTitle(title = "终端日志导出")
@ExportSheet(name = "终端日志", height = 22, freezeHeader = true, filterHeader = true)
public class MachineTerminalLogExportDTO {

    @ApiModelProperty(value = "用户名")
    @ExportField(index = 0, header = "用户名", width = 17, wrapText = true)
    private String username;

    @ApiModelProperty(value = "机器名称")
    @ExportField(index = 1, header = "机器名称", width = 20, wrapText = true)
    private String machineName;

    @ApiModelProperty(value = "机器标识")
    @ExportField(index = 2, header = "机器标识", width = 20, wrapText = true)
    private String machineTag;

    @ApiModelProperty(value = "机器主机")
    @ExportField(index = 3, header = "机器主机", width = 20, wrapText = true)
    private String machineHost;

    @ApiModelProperty(value = "accessToken")
    @ExportField(index = 4, header = "accessToken", width = 5, hidden = true, wrapText = true)
    private String accessToken;

    @ApiModelProperty(value = "建立连接时间")
    @ExportField(index = 5, header = "建立连接时间", width = 20, wrapText = true, type = ExcelFieldType.DATE, format = Dates.YMD_HMS)
    private Date connectedTime;

    @ApiModelProperty(value = "断开连接时间")
    @ExportField(index = 6, header = "断开连接时间", width = 20, wrapText = true, type = ExcelFieldType.DATE, format = Dates.YMD_HMS)
    private Date disconnectedTime;

    @ApiModelProperty(value = "close code")
    @ExportField(index = 7, header = "close code", width = 11, wrapText = true)
    private Integer closeCode;

    @ApiModelProperty(value = "录屏文件路径")
    @ExportField(index = 8, header = "录屏文件路径", width = 35, wrapText = true)
    private String screenPath;

    static {
        TypeStore.STORE.register(MachineTerminalLogDO.class, MachineTerminalLogExportDTO.class, p -> {
            MachineTerminalLogExportDTO dto = new MachineTerminalLogExportDTO();
            dto.setUsername(p.getUsername());
            dto.setMachineName(p.getMachineName());
            dto.setMachineTag(p.getMachineTag());
            dto.setMachineHost(p.getMachineHost());
            dto.setAccessToken(p.getAccessToken());
            dto.setConnectedTime(p.getConnectedTime());
            dto.setDisconnectedTime(p.getDisconnectedTime());
            dto.setCloseCode(p.getCloseCode());
            dto.setScreenPath(p.getScreenPath());
            return dto;
        });
    }

}
