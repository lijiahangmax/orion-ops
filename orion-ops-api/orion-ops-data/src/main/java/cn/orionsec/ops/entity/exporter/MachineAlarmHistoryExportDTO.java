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
package cn.orionsec.ops.entity.exporter;

import cn.orionsec.ops.constant.machine.MachineAlarmType;
import cn.orionsec.ops.entity.domain.MachineAlarmHistoryDO;
import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.office.excel.annotation.ExportField;
import com.orion.office.excel.annotation.ExportFont;
import com.orion.office.excel.annotation.ExportSheet;
import com.orion.office.excel.annotation.ExportTitle;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 机器报警记录导出
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 10:24
 */
@Data
@ApiModel(value = "机器报警记录导出")
@ExportTitle(title = "机器报警记录导出")
@ExportSheet(name = "机器报警记录", height = 22, freezeHeader = true, filterHeader = true)
public class MachineAlarmHistoryExportDTO {

    @ApiModelProperty(value = "报警机器名称")
    @ExportField(index = 0, header = "报警机器名称", width = 24, wrapText = true)
    private String name;

    @ApiModelProperty(value = "报警机器主机")
    @ExportField(index = 1, header = "报警机器主机", width = 20, wrapText = true)
    private String host;

    /**
     * @see MachineAlarmType
     */
    @ApiModelProperty(value = "报警类型 10: cpu使用率 20: 内存使用率")
    @ExportField(index = 2, header = "报警类型", width = 18, wrapText = true)
    private String alarmType;

    @ApiModelProperty(value = "报警值")
    @ExportField(index = 3, header = "报警值", width = 15, wrapText = true)
    @ExportFont(color = "#F5222D")
    private Double alarmValue;

    @ApiModelProperty(value = "报警时间")
    @ExportField(index = 4, header = "报警时间", width = 25, format = Dates.YMD_HMS)
    private Date alarmTime;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    static {
        TypeStore.STORE.register(MachineAlarmHistoryDO.class, MachineAlarmHistoryExportDTO.class, p -> {
            MachineAlarmHistoryExportDTO dto = new MachineAlarmHistoryExportDTO();
            dto.setMachineId(p.getMachineId());
            dto.setAlarmType(MachineAlarmType.of(p.getAlarmType()).getLabel());
            dto.setAlarmValue(p.getAlarmValue());
            dto.setAlarmTime(p.getAlarmTime());
            return dto;
        });
    }

}
