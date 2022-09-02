package com.orion.ops.mapping.history;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.entity.domain.HistoryValueSnapshotDO;
import com.orion.ops.entity.vo.history.HistoryValueVO;

/**
 * 历史值 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 18:08
 */
public class HistoryValueConversion {

    static {
        TypeStore.STORE.register(HistoryValueSnapshotDO.class, HistoryValueVO.class, p -> {
            HistoryValueVO vo = new HistoryValueVO();
            vo.setId(p.getId());
            vo.setType(p.getOperatorType());
            vo.setBeforeValue(p.getBeforeValue());
            vo.setAfterValue(p.getAfterValue());
            vo.setUpdateUserId(p.getUpdateUserId());
            vo.setUpdateUserName(p.getUpdateUserName());
            vo.setCreateTime(p.getCreateTime());
            vo.setCreateTimeAgo(Dates.ago(p.getCreateTime()));
            return vo;
        });
    }

}
