package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.domain.ApplicationInfoDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 应用详情响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/5 18:52
 */
@Data
@ApiModel(value = "应用详情响应")
public class ApplicationDetailVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "应用唯一标识")
    private String tag;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "应用版本仓库id")
    private Long vcsId;

    @ApiModelProperty(value = "应用版本仓库名称")
    private String vcsName;

    @ApiModelProperty(value = "配置环境变量")
    private ApplicationConfigEnvVO env;

    @ApiModelProperty(value = "构建流程")
    private List<ApplicationActionVO> buildActions;

    @ApiModelProperty(value = "关联机器")
    private List<ApplicationMachineVO> releaseMachines;

    @ApiModelProperty(value = "发布流程")
    private List<ApplicationActionVO> releaseActions;

    static {
        TypeStore.STORE.register(ApplicationInfoDO.class, ApplicationDetailVO.class, p -> {
            ApplicationDetailVO vo = new ApplicationDetailVO();
            vo.setId(p.getId());
            vo.setName(p.getAppName());
            vo.setTag(p.getAppTag());
            vo.setDescription(p.getDescription());
            vo.setVcsId(p.getVcsId());
            return vo;
        });
    }

}
