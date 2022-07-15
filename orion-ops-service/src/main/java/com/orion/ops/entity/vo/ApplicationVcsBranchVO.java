package com.orion.ops.entity.vo;

import com.orion.ext.vcs.git.info.BranchInfo;
import com.orion.lang.utils.convert.TypeStore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 应用分支信息响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/12 17:56
 */
@Data
@ApiModel(value = "应用分支信息响应")
public class ApplicationVcsBranchVO {

    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * @see com.orion.ops.constant.Const#IS_DEFAULT
     */
    @ApiModelProperty(value = "是否为默认")
    private Integer isDefault;

    static {
        TypeStore.STORE.register(BranchInfo.class, ApplicationVcsBranchVO.class, p -> {
            ApplicationVcsBranchVO vo = new ApplicationVcsBranchVO();
            vo.setName(p.toString());
            return vo;
        });
    }

}
