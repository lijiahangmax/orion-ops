package com.orion.ops.entity.dto;

import com.orion.ops.entity.domain.ApplicationProfileDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

/**
 * 应用环境缓存
 *
 * @author Jiahang Li
 * @since 2021-07-02
 */
@Data
public class ApplicationProfileDTO {

    /**
     * id
     */
    private Long id;

    /**
     * 环境名称
     */
    private String profileName;

    /**
     * 环境标识
     */
    private String profileTag;

    static {
        TypeStore.STORE.register(ApplicationProfileDO.class, ApplicationProfileDTO.class, p -> {
            ApplicationProfileDTO dto = new ApplicationProfileDTO();
            dto.setId(p.getId());
            dto.setProfileName(p.getProfileName());
            dto.setProfileTag(p.getProfileTag());
            return dto;
        });
    }

}
