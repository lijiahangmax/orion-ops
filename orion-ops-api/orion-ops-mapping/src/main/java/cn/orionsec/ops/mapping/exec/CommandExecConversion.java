/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.mapping.exec;

import cn.orionsec.kit.lang.utils.convert.TypeStore;
import cn.orionsec.kit.lang.utils.time.Dates;
import cn.orionsec.ops.entity.domain.CommandExecDO;
import cn.orionsec.ops.entity.vo.exec.CommandExecStatusVO;
import cn.orionsec.ops.entity.vo.exec.CommandExecVO;
import cn.orionsec.ops.utils.Utils;

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
