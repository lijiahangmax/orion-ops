package com.orion.ops.consts.system;

import com.orion.function.Conversion;
import com.orion.ops.consts.EnableType;
import com.orion.ops.consts.MessageConst;
import com.orion.utils.Strings;
import com.orion.utils.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 系统配置项
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/18 21:16
 */
@AllArgsConstructor
@Getter
public enum SystemConfigKey {

    /**
     * 文件清理阈值 (天)
     */
    FILE_CLEAN_THRESHOLD(10, SystemEnvAttr.FILE_CLEAN_THRESHOLD) {
        @Override
        protected boolean valid(String s) {
            return Strings.isInteger(s) && Integer.parseInt(s) >= 0;
        }
    },

    /**
     * 是否启用自动清理
     */
    ENABLE_AUTO_CLEAN_FILE(20, SystemEnvAttr.ENABLE_AUTO_CLEAN_FILE) {
        @Override
        protected String conversionValue(String s) {
            return ENABLED_TYPE.apply(s);
        }
    },

    /**
     * 是否允许多端登陆
     */
    ALLOW_MULTIPLE_LOGIN(30, SystemEnvAttr.ALLOW_MULTIPLE_LOGIN) {
        @Override
        protected String conversionValue(String s) {
            return ENABLED_TYPE.apply(s);
        }
    },

    /**
     * 是否启用登陆失败锁定
     */
    LOGIN_FAILURE_LOCK(40, SystemEnvAttr.LOGIN_FAILURE_LOCK) {
        @Override
        protected String conversionValue(String s) {
            return ENABLED_TYPE.apply(s);
        }
    },

    /**
     * 是否启用登陆IP绑定
     */
    LOGIN_IP_BIND(50, SystemEnvAttr.LOGIN_IP_BIND) {
        @Override
        protected String conversionValue(String s) {
            return ENABLED_TYPE.apply(s);
        }
    },

    /**
     * 是否启用凭证自动续签
     */
    LOGIN_TOKEN_AUTO_RENEW(60, SystemEnvAttr.LOGIN_TOKEN_AUTO_RENEW) {
        @Override
        protected String conversionValue(String s) {
            return ENABLED_TYPE.apply(s);
        }
    },

    /**
     * 登陆凭证有效期 (时)
     */
    LOGIN_TOKEN_EXPIRE(70, SystemEnvAttr.LOGIN_TOKEN_EXPIRE) {
        @Override
        protected boolean valid(String s) {
            return Strings.isInteger(s) && Integer.parseInt(s) > 0;
        }
    },

    /**
     * 登陆失败锁定阈值 (次)
     */
    LOGIN_FAILURE_LOCK_THRESHOLD(80, SystemEnvAttr.LOGIN_FAILURE_LOCK_THRESHOLD) {
        @Override
        protected boolean valid(String s) {
            return Strings.isInteger(s) && Integer.parseInt(s) > 0;
        }
    },

    /**
     * 登陆自动续签阈值 (时)
     */
    LOGIN_TOKEN_AUTO_RENEW_THRESHOLD(90, SystemEnvAttr.LOGIN_TOKEN_AUTO_RENEW_THRESHOLD) {
        @Override
        protected boolean valid(String s) {
            return Strings.isInteger(s) && Integer.parseInt(s) > 0;
        }
    },

    /**
     * 自动恢复启用的调度任务
     */
    RESUME_ENABLE_SCHEDULER_TASK(100, SystemEnvAttr.RESUME_ENABLE_SCHEDULER_TASK) {
        @Override
        protected String conversionValue(String s) {
            return ENABLED_TYPE.apply(s);
        }
    },

    ;

    private static final Conversion<String, String> ENABLED_TYPE = s -> EnableType.of(Boolean.valueOf(s)).getLabel();

    private final Integer type;

    private final SystemEnvAttr env;

    /**
     * 验证
     *
     * @param s s
     * @return value
     */
    protected boolean valid(String s) {
        return true;
    }

    /**
     * 转化值
     *
     * @param s s
     * @return value
     */
    protected String conversionValue(String s) {
        return s;
    }

    /**
     * 获取值
     *
     * @return value
     */
    public String getValue(String s) {
        // 验证
        Valid.isTrue(this.valid(s), MessageConst.INVALID_CONFIG);
        // 转化
        return this.conversionValue(s);
    }

    public static SystemConfigKey of(Integer type) {
        if (type == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(a -> a.type.equals(type))
                .findFirst()
                .orElse(null);
    }

}
