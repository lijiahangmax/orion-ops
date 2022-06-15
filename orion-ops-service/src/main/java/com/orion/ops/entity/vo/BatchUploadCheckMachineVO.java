package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.utils.convert.TypeStore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 批量上传文件检查机器响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/13 16:30
 */
@Data
@ApiModel(value = "批量上传文件检查机器响应")
public class BatchUploadCheckMachineVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "主机")
    private String host;

    static {
        TypeStore.STORE.register(MachineInfoDO.class, BatchUploadCheckMachineVO.class, p -> {
            BatchUploadCheckMachineVO vo = new BatchUploadCheckMachineVO();
            vo.setId(p.getId());
            vo.setName(p.getMachineName());
            vo.setHost(p.getMachineHost());
            return vo;
        });
    }

}
