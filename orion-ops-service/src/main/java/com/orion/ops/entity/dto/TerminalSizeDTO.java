package com.orion.ops.entity.dto;

import com.orion.lang.utils.Strings;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.function.Supplier;

/**
 * 终端大小参数
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/12 13:58
 */
@Data
@ApiModel(value = "终端大小参数")
public class TerminalSizeDTO {

    @ApiModelProperty(value = "列数")
    private Integer cols;

    @ApiModelProperty(value = "行数")
    private Integer rows;

    /**
     * 解析 body
     * <p>
     * .e.g cols|rows
     *
     * @param body body
     * @return connect
     */
    public static TerminalSizeDTO parse(String body) {
        String[] conf = body.split("\\|");
        if (conf.length != 2) {
            return null;
        }
        // 解析size
        return parseSize(conf, TerminalSizeDTO::new);
    }

    /**
     * 设置 size
     * <p>
     * cols|rows
     *
     * @param arr arr
     */
    protected static <T extends TerminalSizeDTO> T parseSize(String[] arr, Supplier<T> sup) {
        if (!Strings.isInteger(arr[0]) || !Strings.isInteger(arr[1])) {
            return null;
        }
        T size = sup.get();
        size.setCols(Integer.parseInt(arr[0]));
        size.setRows(Integer.parseInt(arr[1]));
        return size;
    }

}
