package com.orion.ops.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 终端接入监视
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/7/29 10:11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TerminalWatcherDTO {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 会话token
     */
    private String token;

    /**
     * 是否只读
     *
     * @see com.orion.ops.constant.Const#ENABLE
     */
    private Integer readonly;

}
