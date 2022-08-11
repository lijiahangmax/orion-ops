package com.orion.ops.handler.terminal.screen;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * terminal 录屏头
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/7/26 18:21
 */
@Data
public class TerminalScreenHeader {

    private static final Integer CAST_VERSION = 2;

    /**
     * 版本
     */
    private Integer version;

    /**
     * 标题
     */
    private String title;

    /**
     * cols
     */
    @JSONField(name = "width")
    private Integer cols;

    /**
     * rows
     */
    @JSONField(name = "height")
    private Integer rows;

    /**
     * 开始时间戳
     */
    private Long timestamp;

    /**
     * 环境变量
     */
    private TerminalScreenEnv env;

    public TerminalScreenHeader() {
        this.version = CAST_VERSION;
    }

}
