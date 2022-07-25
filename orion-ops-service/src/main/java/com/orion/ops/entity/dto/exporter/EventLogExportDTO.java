package com.orion.ops.entity.dto.exporter;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.office.excel.annotation.ExportField;
import com.orion.office.excel.annotation.ExportSheet;
import com.orion.office.excel.annotation.ExportTitle;
import com.orion.office.excel.type.ExcelFieldType;
import com.orion.ops.constant.event.EventClassify;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.entity.domain.UserEventLogDO;
import com.orion.ops.utils.Utils;
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

    @ApiModelProperty(value = "事件分类")
    @ExportField(index = 1, header = "事件分类", width = 17)
    private String classify;

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
