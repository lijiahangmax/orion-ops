package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 站内信请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/25 16:19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "站内信请求")
public class WebSideMessageRequest extends PageRequest {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "id")
    private List<Long> idList;

    /**
     * @see com.orion.ops.consts.message.MessageClassify
     */
    @ApiModelProperty(value = "消息分类")
    private Integer classify;

    /**
     * @see com.orion.ops.consts.message.MessageType
     */
    @ApiModelProperty(value = "消息类型")
    private Integer type;

    /**
     * @see com.orion.ops.consts.message.ReadStatus
     */
    @ApiModelProperty(value = "是否已读 1未读 2已读")
    private Integer status;

    @ApiModelProperty(value = "消息")
    private String message;

    @ApiModelProperty(value = "最大id")
    private Long maxId;

    @ApiModelProperty(value = "开始时间区间-开始")
    private Date rangeStart;

    @ApiModelProperty(value = "开始时间区间-结束")
    private Date rangeEnd;

}
