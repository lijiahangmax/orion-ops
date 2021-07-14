package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 上线单请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/12 28:54
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicationReleaseBillRequest extends PageRequest {

    /**
     * 上线单id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * appId
     */
    private Long appId;

    /**
     * profileId
     */
    private Long profileId;

    /**
     * 状态
     *
     * @see com.orion.ops.consts.app.ReleaseStatus
     */
    private Integer status;

    /**
     * 只看自己
     *
     * @see com.orion.ops.consts.Const#ENABLE
     */
    private Integer onlyMyself;

}
