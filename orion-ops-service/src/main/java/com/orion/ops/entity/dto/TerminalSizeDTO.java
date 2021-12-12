package com.orion.ops.entity.dto;

import com.orion.utils.Strings;
import lombok.Data;

import java.util.function.Supplier;

/**
 * terminal 大小参数
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/12 13:58
 */
@Data
public class TerminalSizeDTO {

    /**
     * 行字数
     */
    private Integer cols;

    /**
     * 行数
     */
    private Integer rows;

    /**
     * 宽px
     */
    private Integer width;

    /**
     * 高px
     */
    private Integer height;

    /**
     * 解析 body
     * <p>
     * .e.g cols|rows|width|height
     *
     * @param body body
     * @return connect
     */
    public static TerminalSizeDTO parse(String body) {
        String[] conf = body.split("\\|");
        if (conf.length != 4) {
            return null;
        }
        // 解析size
        return parseSize(conf, TerminalSizeDTO::new);
    }

    /**
     * 设置 size
     * <p>
     * cols|rows|width|height
     *
     * @param arr arr
     */
    protected static <T extends TerminalSizeDTO> T parseSize(String[] arr, Supplier<T> sup) {
        if (!Strings.isInteger(arr[0]) || !Strings.isInteger(arr[1]) ||
                !Strings.isInteger(arr[2]) || !Strings.isInteger(arr[3])) {
            return null;
        }
        T size = sup.get();
        size.setCols(Integer.parseInt(arr[0]));
        size.setRows(Integer.parseInt(arr[1]));
        size.setWidth(Integer.parseInt(arr[2]));
        size.setHeight(Integer.parseInt(arr[3]));
        return size;
    }

}
