package com.orion.ops.utils;

import com.orion.ops.consts.Const;
import com.orion.utils.crypto.enums.CipherAlgorithm;
import com.orion.utils.crypto.enums.PaddingMode;
import com.orion.utils.crypto.symmetric.EcbSymmetric;

/**
 * 密码混入器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/14 23:27
 */
public class ValueMix {

    private static final EcbSymmetric ECB = new EcbSymmetric(CipherAlgorithm.AES, PaddingMode.ZERO_PADDING);

    private ValueMix() {
    }

    /**
     * 加密
     *
     * @param c 明文
     * @return 密文
     */
    public static String encrypt(String c) {
        return ECB.encrypt(c, Const.ORION_OPS);
    }

    /**
     * 加密
     *
     * @param c   明文
     * @param key key
     * @return 密文
     */
    public static String encrypt(String c, String key) {
        return ECB.encrypt(c, key);
    }

    /**
     * 解密
     *
     * @param de 密文
     * @return 明文
     */
    public static String decrypt(String de) {
        return ECB.decrypt(de, Const.ORION_OPS);
    }

    /**
     * 解密
     *
     * @param de  密文
     * @param key key
     * @return 明文
     */
    public static String decrypt(String de, String key) {
        return ECB.decrypt(de, key);
    }

}
