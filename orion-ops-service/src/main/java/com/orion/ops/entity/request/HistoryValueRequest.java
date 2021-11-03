package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 历史值快照请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/9 19:03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HistoryValueRequest extends PageRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 值id
     */
    private Long valueId;

    /**
     * 值类型
     *
     * @see com.orion.ops.consts.HistoryValueType
     */
    private Integer valueType;

}
