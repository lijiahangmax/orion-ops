package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.CommandExecDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

/**
 * 命令执行状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/12 18:03
 */
@Data
public class CommandExecStatusVO {

    /**
     * id
     */
    private Long id;

    /**
     * exitCode
     */
    private Integer exitCode;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 使用时间 ms
     */
    private Long used;

    /**
     * 使用时间
     */
    private String keepTime;

    static {
        TypeStore.STORE.register(CommandExecDO.class, CommandExecStatusVO.class, p -> {
            CommandExecStatusVO vo = new CommandExecStatusVO();
            vo.setId(p.getId());
            vo.setExitCode(p.getExitCode());
            vo.setStatus(p.getExecStatus());
            if (p.getStartDate() != null && p.getEndDate() != null) {
                vo.setUsed(p.getEndDate().getTime() - p.getStartDate().getTime());
                vo.setKeepTime(Dates.interval(vo.getUsed(), false, "d", "h", "m", "s"));
            }
            return vo;
        });
    }

}
