package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.utils.convert.TypeStore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 批量上传文件检查文件响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/13 16:30
 */
@Data
@ApiModel(value = "批量上传文件检查文件响应")
public class BatchUploadCheckFileVO {

    @ApiModelProperty(value = "机器id")
    private Long id;

    @ApiModelProperty(value = "机器名称")
    private String name;

    @ApiModelProperty(value = "机器主机")
    private String host;

    @ApiModelProperty(value = "已存在的文件")
    private List<String> presentFiles;

    static {
        TypeStore.STORE.register(MachineInfoDO.class, BatchUploadCheckFileVO.class, p -> {
            BatchUploadCheckFileVO vo = new BatchUploadCheckFileVO();
            vo.setId(p.getId());
            vo.setName(p.getMachineName());
            vo.setHost(p.getMachineHost());
            return vo;
        });
    }

}
