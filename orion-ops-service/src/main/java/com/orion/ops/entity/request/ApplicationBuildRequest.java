package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 应用构建请求
 *
 * @author Jiahang Li
 * @since 2021-12-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicationBuildRequest extends PageRequest {

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private List<Long> idList;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 环境id
     */
    private Long profileId;

    /**
     * 构建序列
     */
    private Integer seq;

    /**
     * 构建分支
     */
    private String branchName;

    /**
     * 构建提交id
     */
    private String commitId;

    /**
     * 状态 10未开始 20执行中 30已完成 40执行失败 50已取消
     *
     * @see com.orion.ops.consts.app.BuildStatus
     */
    private Integer status;

    /**
     * 描述
     */
    private String description;

    /**
     * 只看自己
     *
     * @see com.orion.ops.consts.Const#ENABLE
     */
    private Integer onlyMyself;

    /**
     * 命令
     */
    private String command;

}
