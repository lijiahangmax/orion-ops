package com.orion.ops;

import com.orion.ops.consts.Const;
import com.orion.ops.utils.ValueMix;
import org.junit.Test;

/**
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/30 20:50
 */
public class ValueMixTests {

    @Test
    public void machinePassword() {
        String password = "123123123123";
        Long machineId = 1L;
        System.out.println(ValueMix.encrypt(password, machineId + Const.ORION_OPS));
    }

}
