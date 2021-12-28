package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationMachineDO;
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
     * tag
     */
    private String machineTag;

    /**
     * 当前版本发布id
     */
    private Long releaseId;

    /**
     * 当前版本构建id
     */
    private Long buildId;

    /**
     * 当前版本构建序列
     */
    private Long buildSeq;

    static {
        TypeStore.STORE.register(ApplicationMachineDO.class, ApplicationMachineVO.class, p -> {
            ApplicationMachineVO vo = new ApplicationMachineVO();
            vo.setId(p.getId());
            vo.setMachineId(p.getMachineId());
            vo.setReleaseId(p.getReleaseId());
            vo.setBuildId(p.getBuildId());
            vo.setBuildSeq(p.getBuildSeq());
            return vo;
        });
    }

}
