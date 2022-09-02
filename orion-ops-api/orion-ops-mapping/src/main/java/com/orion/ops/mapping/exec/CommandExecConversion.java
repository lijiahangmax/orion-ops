package com.orion.ops.mapping.exec;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.entity.domain.CommandExecDO;
import com.orion.ops.entity.vo.exec.CommandExecStatusVO;
import com.orion.ops.entity.vo.exec.CommandExecVO;
import com.orion.ops.utils.Utils;

import java.util.Date;
import java.util.Optional;

/**
 * 命令执行 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 18:03
 */
public class CommandExecConversion {

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

    static {
        TypeStore.STORE.register(CommandExecDO.class, CommandExecVO.class, p -> {
            CommandExecVO vo = new CommandExecVO();
            Date startDate = p.getStartDate();
            Date endDate = p.getEndDate();
            Date createTime = p.getCreateTime();
            vo.setId(p.getId());
            vo.setUserId(p.getUserId());
            vo.setUsername(p.getUserName());
            vo.setType(p.getExecType());
            vo.setStatus(p.getExecStatus());
            vo.setMachineId(p.getMachineId());
            vo.setMachineName(p.getMachineName());
            vo.setMachineHost(p.getMachineHost());
            vo.setMachineTag(p.getMachineTag());
            vo.setExitCode(p.getExitCode());
            vo.setCommand(p.getExecCommand());
            vo.setDescription(p.getDescription());
            vo.setStartDate(startDate);
            vo.setEndDate(endDate);
            vo.setCreateTime(createTime);
            Optional.ofNullable(startDate).map(Dates::ago).ifPresent(vo::setStartDateAgo);
            Optional.ofNullable(endDate).map(Dates::ago).ifPresent(vo::setEndDateAgo);
            Optional.ofNullable(createTime).map(Dates::ago).ifPresent(vo::setCreateTimeAgo);
            if (startDate != null && endDate != null) {
                vo.setUsed(endDate.getTime() - startDate.getTime());
                vo.setKeepTime(Utils.interval(vo.getUsed()));
            }
            return vo;
        });
    }

}
