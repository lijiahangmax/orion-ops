package com.orion.ops.handler.importer.validator;

/**
 * 数据验证器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/11 18:56
 */
public interface DataValidator {

    /**
     * 验证数据
     *
     * @param o o
     */
    void validData(Object o);

}
