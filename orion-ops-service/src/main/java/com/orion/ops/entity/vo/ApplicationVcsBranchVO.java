package com.orion.ops.entity.vo;

import com.orion.utils.convert.TypeStore;
import com.orion.vcs.git.info.BranchInfo;
import lombok.Data;

/**
 * app分支信息
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/12 17:56
 */
@Data
public class ApplicationVcsBranchVO {

    /**
     * id
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    static {
        TypeStore.STORE.register(BranchInfo.class, ApplicationVcsBranchVO.class, p -> {
            ApplicationVcsBranchVO vo = new ApplicationVcsBranchVO();
            vo.setId(p.getId());
            vo.setName(p.getName());
            return vo;
        });
    }

}
