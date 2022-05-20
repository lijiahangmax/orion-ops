package com.orion.ops.entity.vo;

import lombok.Data;

/**
 * 批量上传 token 返回
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/16 10:44
 */
@Data
public class BatchUploadTokenVO {

    /**
     * accessToken
     */
    private String accessToken;

    /**
     * notifyToken
     */
    private String notifyToken;

}
