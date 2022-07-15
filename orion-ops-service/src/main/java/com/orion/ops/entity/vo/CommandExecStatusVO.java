package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.domain.CommandExecDO;
import com.orion.ops.utils.Utils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/12 18:03
 */
@Data
@ApiModel(value = "命令执行状态响应")
public class CommandExecStatusVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "exitCode")
    private Integer exitCode;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "使用时间毫秒")
    private Long used;

    @ApiModelProperty(value = "使用时间")
    private String keepTime;

    static {
        TypeStore.STORE.register(CommandExecDO.class, CommandExecStatusVO.class, p -> {
            CommandExecStatusVO vo = new CommandExecStatusVO();
            vo.setId(p.getId());
            vo.setExitCode(p.getExitCode());
            vo.setStatus(p.getExecStatus());
            if (p.getStartDate() != null && p.getEndDate() != null) {
                vo.setUsed(p.getEndDate().getTime() - p.getStartDate().getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            return vo;
        });
    }

}
