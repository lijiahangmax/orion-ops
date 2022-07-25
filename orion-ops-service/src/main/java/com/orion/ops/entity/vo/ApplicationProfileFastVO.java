package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.dto.ApplicationProfileDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用环境响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/1/20 15:06
 */
@Data
@ApiModel(value = "应用环境响应")
public class ApplicationProfileFastVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "唯一标识")
    private String tag;

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
