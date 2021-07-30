package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationInfoDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

import java.util.List;

/**
 * 应用详情
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/5 18:52
 */
@Data
public class ApplicationDetailVO {

    /**
     * appId
     */
    private Long id;

    /**
     * app 名称
     */
    private String name;

    /**
     * app 标识符
     */
    private String tag;

    /**
     * app 描述
     */
    private String description;

    /**
     * 机器数量
     */
    private Integer machineCount;

    /**
     * 环境id
     */
    private Long profileId;

    /**
     * 环境名称
     */
    private String profileName;

    /**
     * 环境tag
     */
    private String profileTag;

    /**
     * 版本控制根目录
     */
    private String vcsRootPath;

    /**
     * 代码目录
     */
    private String vcsCodePath;

    /**
     * 版本管理工具
     */
    private String vcsType;

    /**
     * 构建产物目录
     */
    private String distPath;

    /**
     * 关联机器
     */
    private List<ApplicationMachineVO> machines;

    /**
     * 部署步骤
     */
    private List<ApplicationDeployActionVO> actions;

    static {
        TypeStore.STORE.register(ApplicationInfoDO.class, ApplicationDetailVO.class, p -> {
            ApplicationDetailVO vo = new ApplicationDetailVO();
            vo.setId(p.getId());
            vo.setName(p.getAppName());
            vo.setTag(p.getAppTag());
            vo.setDescription(p.getDescription());
            return vo;
        });
    }

}
