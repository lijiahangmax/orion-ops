package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.domain.ApplicationInfoDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 应用信息响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/2 18:58
 */
@Data
@ApiModel(value = "应用信息响应")
public class ApplicationInfoVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "应用唯一标识")
    private String tag;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "应用版本仓库id")
    private Long repoId;

    @ApiModelProperty(value = "应用版本仓库名称")
    private String repoName;

    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * @see com.orion.ops.constant.Const#CONFIGURED
     * @see com.orion.ops.constant.Const#NOT_CONFIGURED
     */
    @ApiModelProperty(value = "是否已经配置 1已配置 2未配置")
    private Integer isConfig;

    @ApiModelProperty(value = "应用机器")
    private List<ApplicationMachineVO> machines;

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

}
