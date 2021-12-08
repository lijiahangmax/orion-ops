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
     * 版本控制id
     */
    private Long vcsId;

    /**
     * 版本控制名称
     */
    private String vcsName;

    /**
     * 构建产物目录
     */
    private String bundlePath;

    /**
     * 产物传输目录
     */
    private String transferPath;

    /**
     * 发布序列 10串行 20并行
     *
     * @see com.orion.ops.consts.app.ReleaseSerialType
     */
    private Integer releaseSerial;

    /**
     * 构建流程
     */
    private List<ApplicationActionVO> buildActions;

    /**
     * 关联机器
     */
    private List<ApplicationMachineVO> releaseMachines;

    /**
     * 发布流程
     */
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
