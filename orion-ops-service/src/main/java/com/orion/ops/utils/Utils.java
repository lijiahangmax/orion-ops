package com.orion.ops.utils;

import com.orion.id.UUIds;
import com.orion.ops.consts.Const;
import com.orion.utils.Strings;

/**
 * 工具类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/3 9:28
 */
public class Utils {

    private Utils() {
    }

    /**
     * 获取复制后缀
     *
     * @return suffix
     */
    public static String getCopySuffix() {
        return getSymbolSuffix(Const.COPY);
    }

    /**
     * 获取后缀
     *
     * @param symbol symbol
     * @return suffix
     */
    public static String getSymbolSuffix(String symbol) {
        return " - " + symbol + Strings.SPACE + UUIds.random32().substring(0, 5).toUpperCase();
    }

}
