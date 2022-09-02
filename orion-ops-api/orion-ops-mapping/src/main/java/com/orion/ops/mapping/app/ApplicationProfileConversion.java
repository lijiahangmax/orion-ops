package com.orion.ops.mapping.app;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.domain.ApplicationProfileDO;
import com.orion.ops.entity.dto.app.ApplicationProfileDTO;
import com.orion.ops.entity.vo.app.ApplicationProfileFastVO;
import com.orion.ops.entity.vo.app.ApplicationProfileVO;

/**
 * 应用环境 对象转换器
 *
 * @author Jiahang Li
 * @since 2021-07-02
 */
public class ApplicationProfileConversion {

    static {
        TypeStore.STORE.register(ApplicationProfileDO.class, ApplicationProfileVO.class, p -> {
            ApplicationProfileVO vo = new ApplicationProfileVO();
            vo.setId(p.getId());
            vo.setName(p.getProfileName());
            vo.setTag(p.getProfileTag());
            vo.setDescription(p.getDescription());
            vo.setReleaseAudit(p.getReleaseAudit());
            return vo;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationProfileDO.class, ApplicationProfileDTO.class, p -> {
            ApplicationProfileDTO dto = new ApplicationProfileDTO();
            dto.setId(p.getId());
            dto.setProfileName(p.getProfileName());
            dto.setProfileTag(p.getProfileTag());
            return dto;
        });
    }

    static {
        TypeStore.STORE.register(ApplicationProfileDTO.class, ApplicationProfileFastVO.class, p -> {
            ApplicationProfileFastVO vo = new ApplicationProfileFastVO();
            vo.setId(p.getId());
            vo.setName(p.getProfileName());
            vo.setTag(p.getProfileTag());
            return vo;
        });
    }

}
