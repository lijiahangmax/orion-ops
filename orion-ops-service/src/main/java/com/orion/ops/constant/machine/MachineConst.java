package com.orion.ops.constant.machine;

import com.orion.ops.constant.Const;

/**
 * 机器常量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/4 18:40
 */
public class MachineConst {

    private MachineConst() {
    }

    /**
     * 远程连接尝试次数
     */
    public static final int CONNECT_RETRY_TIMES = 1;

    /**
     * 远程连接超时时间
     */
    public static final int CONNECT_TIMEOUT = Const.MS_S_5;

}
