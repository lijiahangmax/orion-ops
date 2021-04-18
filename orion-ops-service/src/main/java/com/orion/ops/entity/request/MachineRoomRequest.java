package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 机房请求
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/14 20:27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MachineRoomRequest extends PageRequest {

    /**
     * 机房id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 标识
     */
    private String tag;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态 1正常 2关闭
     */
    private Integer status;

    /**
     * 负责人id
     */
    private Long managerId;

}
