package com.orion.ops.entity.request;

import com.orion.lang.wrapper.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 环境变量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/15 11:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MachineEnvRequest extends PageRequest {

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private List<Long> idList;

    /**
     * 合并选择的目标id
     */
    private Long sourceMachineId;

    /**
     * 合并选择的目标机器id
     */
    private Long targetMachineId;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * key
     */
    private String key;

    /**
     * value
     */
    private String value;

    /**
     * 描述
     */
    private String description;

}
