package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationBuildDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

import java.util.Date;

/**
 * 发布构建列表
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/24 9:54
 */
@Data
public class ApplicationBuildReleaseListVO {

    /**
     * id
     */
    private Long id;

    /**
     * 构建序列
     */
    private Integer seq;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private Date createTime;

    static {
        TypeStore.STORE.register(ApplicationBuildDO.class, ApplicationBuildReleaseListVO.class, p -> {
            ApplicationBuildReleaseListVO vo = new ApplicationBuildReleaseListVO();
            vo.setId(p.getId());
            vo.setSeq(p.getBuildSeq());
            vo.setDescription(p.getDescription());
            vo.setCreateTime(p.getCreateTime());
            return vo;
        });
    }

}
