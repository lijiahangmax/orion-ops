package com.orion.ops.consts;

import com.orion.ops.entity.dto.UserDTO;

/**
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/2 9:44
 */
public class UserHolder {

    private static final ThreadLocal<UserDTO> LOCAL = new ThreadLocal<>();

    public static UserDTO get() {
        return LOCAL.get();
    }

    public static void set(UserDTO user) {
        LOCAL.set(user);
    }

    public static void remove() {
        LOCAL.remove();
    }

}
