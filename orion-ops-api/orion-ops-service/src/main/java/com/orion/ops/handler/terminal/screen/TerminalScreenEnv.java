package com.orion.ops.handler.terminal.screen;

import com.alibaba.fastjson.annotation.JSONField;
import com.orion.net.remote.TerminalType;
import com.orion.ops.constant.Const;
import lombok.Data;

/**
 * terminal 录屏环境
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/7/26 18:27
 */
@Data
public class TerminalScreenEnv {

    /**
     * 终端类型
     */
    @JSONField(name = "TERM")
    private String term;

    /**
     * shell 类型
     */
    @JSONField(name = "SHELL")
    private String shell;

    public TerminalScreenEnv() {
        this(TerminalType.XTERM.getType(), Const.DEFAULT_SHELL);
    }

    public TerminalScreenEnv(String term) {
        this(term, Const.DEFAULT_SHELL);
    }

    public TerminalScreenEnv(String term, String shell) {
        this.term = term;
        this.shell = shell;
    }

}
