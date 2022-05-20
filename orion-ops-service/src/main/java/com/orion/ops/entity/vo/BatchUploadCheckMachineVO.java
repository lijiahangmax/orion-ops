package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

/**
 * 批量上传文件检查机器vo
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/13 16:30
 */
@Data
public class BatchUploadCheckMachineVO {

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 主机
     */
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
