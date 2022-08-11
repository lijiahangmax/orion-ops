package com.orion.ops.mapping.app;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.domain.ApplicationInfoDO;
import com.orion.ops.entity.vo.app.ApplicationDetailVO;
import com.orion.ops.entity.vo.app.ApplicationInfoVO;

/**
 * 应用信息 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 17:39
 */
public class ApplicationConversion {

    static {
        TypeStore.STORE.register(ApplicationInfoDO.class, ApplicationInfoVO.class, p -> {
            ApplicationInfoVO vo = new ApplicationInfoVO();
            vo.setId(p.getId());
            vo.setName(p.getAppName());
            vo.setTag(p.getAppTag());
            vo.setSort(p.getAppSort());
            vo.setRepoId(p.getRepoId());
            vo.setDescription(p.getDescription());
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationInfoDO.class, ApplicationDetailVO.class, p -> {
            ApplicationDetailVO vo = new ApplicationDetailVO();
            vo.setId(p.getId());
            vo.setName(p.getAppName());
            vo.setTag(p.getAppTag());
            vo.setDescription(p.getDescription());
            vo.setRepoId(p.getRepoId());
            return vo;
        });
    }

}
