package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

/**
 * 应用关联机器信息
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/5 19:07
 */
@Data
public class ApplicationMachineVO {

    /**
     * id
     */
    private Long id;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 机器名称
     */
    private String machineName;

    /**
     * 机器主机
     */
    private String machineHost;

    /**
     * 当前版本上线单id
     */
    private Long releaseId;

    /**
     * 发布分支
     */
    private String branchName;

    /**
     * 发布提交id
     */
    private String commitId;

    static {
        TypeStore.STORE.register(MachineInfoDO.class, ApplicationMachineVO.class, p -> {
            ApplicationMachineVO vo = new ApplicationMachineVO();
            vo.setMachineId(p.getId());
            vo.setMachineName(p.getMachineName());
            vo.setMachineHost(p.getMachineHost());
            return vo;
        });
    }

}
