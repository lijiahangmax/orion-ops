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

import cn.orionsec.kit.lang.utils.convert.TypeStore;
import cn.orionsec.kit.lang.utils.time.Dates;
import cn.orionsec.kit.office.excel.annotation.ExportField;
import cn.orionsec.kit.office.excel.annotation.ExportSheet;
import cn.orionsec.kit.office.excel.annotation.ExportTitle;
import cn.orionsec.kit.office.excel.type.ExcelFieldType;
import cn.orionsec.ops.constant.event.EventClassify;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.entity.domain.UserEventLogDO;
import cn.orionsec.ops.utils.Utils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

/**
 * 站内信导出
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/30 14:02
 */
@Data
@ApiModel(value = "站内信导出")
@ExportTitle(title = "操作日志导出")
@ExportSheet(name = "操作日志", height = 22, freezeHeader = true, filterHeader = true)
public class EventLogExportDTO {

    @ApiModelProperty(value = "用户名")
    @ExportField(index = 0, header = "用户名", width = 15, wrapText = true)
    private String username;

    /**
     * @see EventClassify
     */
    @ApiModelProperty(value = "事件分类")
    @ExportField(index = 1, header = "事件分类", width = 17)
    private String classify;

    /**
     * @see EventType
     */
    @ApiModelProperty(value = "事件类型")
    @ExportField(index = 2, header = "事件类型", width = 22)
    private String type;

    @ApiModelProperty(value = "触发时间")
    @ExportField(index = 3, header = "触发时间", width = 20, wrapText = true, type = ExcelFieldType.DATE, format = Dates.YMD_HMS)
    private Date time;

    @ApiModelProperty(value = "日志信息")
    @ExportField(index = 4, header = "日志信息", width = 70, wrapText = true)
    private String message;

    @ApiModelProperty(value = "参数")
    @ExportField(index = 5, header = "参数", width = 20, wrapText = true)
    private String params;

    static {
        TypeStore.STORE.register(UserEventLogDO.class, EventLogExportDTO.class, p -> {
            EventLogExportDTO dto = new EventLogExportDTO();
            dto.setUsername(p.getUsername());
            Optional.ofNullable(p.getEventClassify())
                    .map(EventClassify::of)
                    .map(EventClassify::getLabel)
                    .ifPresent(dto::setClassify);
            Optional.ofNullable(p.getEventType())
                    .map(EventType::of)
                    .map(EventType::getLabel)
                    .ifPresent(dto::setType);
            dto.setTime(p.getCreateTime());
            dto.setMessage(Utils.cleanStainTag(p.getLogInfo()));
            dto.setParams(p.getParamsJson());
            return dto;
        });
    }

}
