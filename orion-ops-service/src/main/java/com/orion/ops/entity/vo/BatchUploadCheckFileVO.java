package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

import java.util.List;

/**
 * 批量上传文件检查文件vo
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/13 16:30
 */
@Data
public class BatchUploadCheckFileVO {

    /**
     * 机器 id
     */
    private Long id;

    /**
     * 机器 名称
     */
    private String name;

    /**
     * 机器 主机
     */
    private String host;

    /**
     * 已存在的文件
     */
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
