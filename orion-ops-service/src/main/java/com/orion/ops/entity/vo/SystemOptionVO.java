package com.orion.ops.entity.vo;

import lombok.Data;

/**
 * 系统配置
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/18 22:53
 */
@Data
public class SystemOptionVO {

    /**
     * 是否启用自动清理
     *
     * @see com.orion.ops.consts.system.SystemEnvAttr#ENABLE_AUTO_CLEAN_FILE
     */
    private Boolean autoCleanFile;

    /**
     * 文件清理阈值
     *
     * @see com.orion.ops.consts.system.SystemEnvAttr#FILE_CLEAN_THRESHOLD
     */
    private Integer fileCleanThreshold;

    /**
     * 允许多端登陆
     *
     * @see com.orion.ops.consts.system.SystemEnvAttr#ALLOW_MULTIPLE_LOGIN
     */
    private Boolean allowMultipleLogin;

    /**
     * 是否启用登陆失败锁定
     *
     * @see com.orion.ops.consts.system.SystemEnvAttr#LOGIN_FAILURE_LOCK
     */
    private Boolean loginFailureLock;

    /**
     * 是否启用登陆IP绑定
     *
     * @see com.orion.ops.consts.system.SystemEnvAttr#LOGIN_IP_BIND
     */
    private Boolean loginIpBind;

    /**
     * 是否启用登陆IP绑定
     *
     * @see com.orion.ops.consts.system.SystemEnvAttr#LOGIN_TOKEN_AUTO_RENEW
     */
    private Boolean loginTokenAutoRenew;

    /**
     * 登陆凭证有效期
     *
     * @see com.orion.ops.consts.system.SystemEnvAttr#LOGIN_TOKEN_EXPIRE
     */
    private Integer loginTokenExpire;

    /**
     * 登陆失败锁定阈值
     *
     * @see com.orion.ops.consts.system.SystemEnvAttr#LOGIN_FAILURE_LOCK_THRESHOLD
     */
    private Integer loginFailureLockThreshold;

    /**
     * 登陆自动续签阈值
     *
     * @see com.orion.ops.consts.system.SystemEnvAttr#LOGIN_TOKEN_AUTO_RENEW_THRESHOLD
     */
    private Integer loginTokenAutoRenewThreshold;

}
