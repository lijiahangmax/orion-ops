package com.orion.ops.entity.dto;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.domain.ApplicationProfileDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用环境缓存
 *
 * @author Jiahang Li
 * @since 2021-07-02
 */
@Data
@ApiModel(value = "应用环境缓存")
public class ApplicationProfileDTO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "环境名称")
    private String profileName;

    @ApiModelProperty(value = "环境唯一标识")
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
