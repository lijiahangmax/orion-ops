package com.orion.ops.entity.dto.exporter;

import com.orion.office.excel.annotation.ExportField;
import com.orion.office.excel.annotation.ExportSheet;
import com.orion.office.excel.annotation.ExportTitle;
import com.orion.office.excel.type.ExcelFieldType;
import com.orion.ops.consts.message.MessageClassify;
import com.orion.ops.consts.message.MessageType;
import com.orion.ops.consts.message.ReadStatus;
import com.orion.ops.entity.domain.WebSideMessageDO;
import com.orion.ops.utils.Utils;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

/**
 * 站内信导出
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/30 11:09
 */
@Data
@ExportTitle(title = "站内信导出")
@ExportSheet(name = "站内信", height = 22, freezeHeader = true, filterHeader = true)
public class WebSideMessageExportDTO {

    @ExportField(index = 0, header = "收信人", width = 15, wrapText = true)
    private String username;

    @ExportField(index = 1, header = "消息分类", width = 17)
    private String classify;

    @ExportField(index = 2, header = "消息类型", width = 22)
    private String type;

    @ExportField(index = 3, header = "是否已读", width = 10)
    private String status;

    @ExportField(index = 4, header = "发送时间", width = 20, wrapText = true, type = ExcelFieldType.DATE, format = Dates.YMD_HMS)
    private Date time;

    @ExportField(index = 5, header = "消息", width = 65, wrapText = true)
    private String message;

    @ExportField(index = 6, header = "参数", width = 20, wrapText = true)
    private String params;

    static {
        TypeStore.STORE.register(WebSideMessageDO.class, WebSideMessageExportDTO.class, p -> {
            WebSideMessageExportDTO dto = new WebSideMessageExportDTO();
            dto.setUsername(p.getToUserName());
            Optional.ofNullable(p.getMessageClassify())
                    .map(MessageClassify::of)
                    .map(MessageClassify::getLabel)
                    .ifPresent(dto::setClassify);
            Optional.ofNullable(p.getMessageType())
                    .map(MessageType::of)
                    .map(MessageType::getLabel)
                    .ifPresent(dto::setType);
            Optional.ofNullable(p.getReadStatus())
                    .map(ReadStatus::of)
                    .map(ReadStatus::getLabel)
                    .ifPresent(dto::setStatus);
            dto.setTime(p.getCreateTime());
            dto.setMessage(Utils.cleanStainTag(p.getSendMessage()));
            dto.setParams(p.getParamsJson());
            return dto;
        });
    }

}
