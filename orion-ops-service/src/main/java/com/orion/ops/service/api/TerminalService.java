package com.orion.ops.service.api;

import com.orion.ops.entity.domain.MachineTerminalDO;

/**
 * 终端服务
 *
 * @author ljh15
 * @version 1.0.0
 * @since 2021/3/31 17:20
 */
public interface TerminalService {

    /**
     * 默认背景色
     */
    String BACKGROUND_COLOR = "#212529";

    /**
     * 默认字体颜色
     */
    String FONT_COLOR = "#FFFFFF";

    /**
     * 默认字体大小
     */
    int FONT_SIZE = 18;

    /**
     * 获取终端配置
     *
     * @param machineId 机器id
     * @return 配置
     */
    MachineTerminalDO getConfig(Long machineId);


}
