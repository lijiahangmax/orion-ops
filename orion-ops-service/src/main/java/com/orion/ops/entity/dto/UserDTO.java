package com.orion.ops.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/2 16:40
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserDTO implements Serializable {

    private Long id;

}
