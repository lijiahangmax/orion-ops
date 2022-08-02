package com.orion.ops.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 终端连接参数
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/17 20:12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "终端连接参数")
public class TerminalConnectDTO extends TerminalSizeDTO {

    @ApiModelProperty(value = "登陆 token")
    private String loginToken;

    /**
     * .e.g cols|rows|loginToken
     *
     * @param body body
     * @return connect
     */
    @ApiModelProperty(value = "解析body")
    public static TerminalConnectDTO parse(String body) {
        String[] conf = body.split("\\|");
        if (conf.length != 3) {
            return null;
        }
        // 解析 size
        TerminalConnectDTO connect = parseSize(conf, TerminalConnectDTO::new);
        if (connect == null) {
            return null;
        }
        connect.setLoginToken(conf[2]);
        return connect;
    }

}
