package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 应用流水线请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/2 10:23
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicationPipelineRequest extends PageRequest {

    /**
     * id
     */
    private Long id;

    /**
     * idList
     */
    private List<Long> idList;

    /**
     * 环境id
     */
    private List<Long> profileIdList;

    /**
     * 环境id
     */
    private Long profileId;

    /**
     * 流水线名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 详情
     */
    private List<ApplicationPipelineDetailRequest> details;

}
