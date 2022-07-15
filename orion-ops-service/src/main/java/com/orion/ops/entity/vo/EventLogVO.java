package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.entity.domain.UserEventLogDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 操作日志响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/10 16:20
 */
@Data
@ApiModel(value = "操作日志响应")
public class EventLogVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "事件分类")
    private Integer classify;

    @ApiModelProperty(value = "事件类型")
    private Integer type;

    @ApiModelProperty(value = "日志信息")
    private String log;

    @ApiModelProperty(value = "日志参数")
    private String params;

    /**
     * @see com.orion.ops.consts.Const#ENABLE
     * @see com.orion.ops.consts.Const#DISABLE
     */
    @ApiModelProperty(value = "是否执行成功 1成功 2失败")
    private Integer result;

    @ApiModelProperty(value = "操作时间")
    private Date createTime;

    @ApiModelProperty(value = "操作时间")
    private String createTimeAgo;

    static {
        TypeStore.STORE.register(UserEventLogDO.class, EventLogVO.class, p -> {
            EventLogVO vo = new EventLogVO();
            vo.setId(p.getId());
            vo.setUserId(p.getUserId());
            vo.setUsername(p.getUsername());
            vo.setClassify(p.getEventClassify());
            vo.setType(p.getEventType());
            vo.setLog(p.getLogInfo());
            vo.setParams(p.getParamsJson());
            vo.setResult(p.getExecResult());
            vo.setCreateTime(p.getCreateTime());
            vo.setCreateTimeAgo(Dates.ago(p.getCreateTime()));
            return vo;
        });
    }

}
