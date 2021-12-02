package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.ApplicationInfoDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

import java.util.List;

/**
 * 应用信息
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/2 18:58
 */
@Data
public class ApplicationInfoVO {

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 标识符
     */
    private String tag;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 版本控制id
     */
    private Long vcsId;

    /**
     * 版本控制名称
     */
    private String vcsName;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否已经配置
     * 1已配置 2未配置
     *
     * @see com.orion.ops.consts.Const#CONFIGURED
     * @see com.orion.ops.consts.Const#NOT_CONFIGURED
     */
    private Integer isConfig;

    /**
     * 机器
     */
    private List<MachineInfoVO> machines;

    static {
        TypeStore.STORE.register(ApplicationInfoDO.class, ApplicationInfoVO.class, p -> {
            ApplicationInfoVO vo = new ApplicationInfoVO();
            vo.setId(p.getId());
            vo.setName(p.getAppName());
            vo.setTag(p.getAppTag());
            vo.setSort(p.getAppSort());
            vo.setVcsId(p.getVcsId());
            vo.setDescription(p.getDescription());
            return vo;
        });
    }

}
