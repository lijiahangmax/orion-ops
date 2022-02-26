package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.HistoryValueSnapshotDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;

/**
 * 历史值快照 vo
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/9 19:03
 */
@Data
public class HistoryValueVO {

    /**
     * id
     */
    private Long id;

    /**
     * 操作类型 1新增 2修改 3删除
     *
     * @see com.orion.ops.consts.history.HistoryOperator
     */
    private Integer type;

    /**
     * 原始值
     */
    private String beforeValue;

    /**
     * 新值
     */
    private String afterValue;

    /**
     * 修改人id
     */
    private Long updateUserId;

    /**
     * 修改人用户名
     */
    private String updateUserName;

    /**
     * 修改时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private String createTimeAgo;

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
