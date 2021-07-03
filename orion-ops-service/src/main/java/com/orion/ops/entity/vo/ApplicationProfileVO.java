package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationProfileDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

/**
 * 应用环境
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/2 18:22
 */
@Data
public class ApplicationProfileVO {

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * tag
     */
    private String tag;

    /**
     * 描述
     */
    private String description;

    /**
     * 发布是否需要审核 1需要 2无需
     *
     * @see com.orion.ops.consts.Const#ENABLE
     * @see com.orion.ops.consts.Const#DISABLE
     */
    private Integer releaseAudit;

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

}
