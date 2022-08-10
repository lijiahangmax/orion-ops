package com.orion.ops.mapping.app;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.domain.ApplicationMachineDO;
import com.orion.ops.entity.vo.app.ApplicationMachineVO;

/**
 * 应用机器 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 17:41
 */
public class ApplicationMachineConversion {

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
