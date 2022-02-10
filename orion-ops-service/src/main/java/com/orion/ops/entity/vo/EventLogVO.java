package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.UserEventLogDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;

/**
 * 操作日志 vo
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/10 16:20
 */
@Data
public class EventLogVO {

    /**
     * id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 事件分类
     */
    private Integer classify;

    /**
     * 事件类型
     */
    private Integer type;

    /**
     * 日志信息
     */
    private String log;

    /**
     * 日志参数
     */
    private String params;

    /**
     * 是否执行成功 1成功 2失败
     *
     * @see com.orion.ops.consts.Const#ENABLE
     * @see com.orion.ops.consts.Const#DISABLE
     */
    private Integer result;

    /**
     * 操作时间
     */
    private Date createTime;

    /**
     * 操作时间
     */
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
