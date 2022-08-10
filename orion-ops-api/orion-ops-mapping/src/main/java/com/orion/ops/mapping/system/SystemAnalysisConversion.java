package com.orion.ops.mapping.system;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.dto.system.SystemSpaceAnalysisDTO;
import com.orion.ops.entity.vo.system.SystemAnalysisVO;

/**
 * 系统统计分析 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 18:22
 */
public class SystemAnalysisConversion {

    static {
        TypeStore.STORE.register(SystemSpaceAnalysisDTO.class, SystemAnalysisVO.class, p -> {
            SystemAnalysisVO vo = new SystemAnalysisVO();
            vo.setTempFileCount(p.getTempFileCount());
            vo.setTempFileSize(p.getTempFileSize());
            vo.setLogFileCount(p.getLogFileCount());
            vo.setLogFileSize(p.getLogFileSize());
            vo.setSwapFileCount(p.getSwapFileCount());
            vo.setSwapFileSize(p.getSwapFileSize());
            vo.setDistVersionCount(p.getDistVersionCount());
            vo.setDistFileSize(p.getDistFileSize());
            vo.setRepoVersionCount(p.getRepoVersionCount());
            vo.setRepoVersionFileSize(p.getRepoVersionFileSize());
            vo.setScreenFileCount(p.getScreenFileCount());
            vo.setScreenFileSize(p.getScreenFileSize());
            return vo;
        });
    }

}
