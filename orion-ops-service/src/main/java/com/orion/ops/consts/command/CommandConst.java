package com.orion.ops.consts.command;

import com.orion.ops.consts.EnvConst;

/**
 * 命令常量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 11:21
 */
public class CommandConst {

    private CommandConst() {
    }

    public static final String TAIL_FILE_DEFAULT = "tail -f -n "
            + EnvConst.getReplaceVariable(EnvConst.OFFSET)
            + " " + EnvConst.getReplaceVariable(EnvConst.FILE);

}
