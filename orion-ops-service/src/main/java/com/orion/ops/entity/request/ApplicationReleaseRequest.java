package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 应用发布请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/20 9:40
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicationReleaseRequest extends PageRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 环境id
     */
    private Long profileId;

    /**
     * 构建id
     */
    private Long buildId;

    /**
     * app机器id列表
     */
    private List<Long> machineIdList;

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
