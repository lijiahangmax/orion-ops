package com.orion.ops.entity.vo;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * 批量上传文件检查vo
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/13 16:29
 */
@Data
public class BatchUploadCheckVO {

    /**
     * 可连接的机器id
     */
    private Set<Long> connectedMachineIdList;

    /**
     * 可以连接的机器
     */
    private List<BatchUploadCheckMachineVO> connectedMachines;

    /**
     * 无法连接的机器
     */
    private List<BatchUploadCheckMachineVO> notConnectedMachines;

    /**
     * 机器已存在的文件
     */
    private List<BatchUploadCheckFileVO> machinePresentFiles;

}
