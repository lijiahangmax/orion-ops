package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationPipelineDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 应用流水线
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/2 10:30
 */
@Data
public class ApplicationPipelineVO {

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 环境id
     */
    private Long profileId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 详情
     */
    private List<ApplicationPipelineDetailVO> details;

    static {
        TypeStore.STORE.register(ApplicationPipelineDO.class, ApplicationPipelineVO.class, p -> {
            ApplicationPipelineVO vo = new ApplicationPipelineVO();
            vo.setId(p.getId());
            vo.setName(p.getPipelineName());
            vo.setDescription(p.getDescription());
            vo.setProfileId(p.getProfileId());
            vo.setCreateTime(p.getCreateTime());
            vo.setUpdateTime(p.getUpdateTime());
            return vo;
        });
    }

}
