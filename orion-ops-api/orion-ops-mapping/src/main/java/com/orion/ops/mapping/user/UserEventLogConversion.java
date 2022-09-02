package com.orion.ops.mapping.user;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.entity.domain.UserEventLogDO;
import com.orion.ops.entity.vo.user.UserEventLogVO;

/**
 * 用户操作日志 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 18:05
 */
public class UserEventLogConversion {

    static {
        TypeStore.STORE.register(UserEventLogDO.class, UserEventLogVO.class, p -> {
            UserEventLogVO vo = new UserEventLogVO();
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
