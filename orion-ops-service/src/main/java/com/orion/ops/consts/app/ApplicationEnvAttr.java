package com.orion.ops.consts.app;

import com.orion.ops.consts.Const;
import com.orion.ops.entity.domain.ApplicationEnvDO;
import com.orion.utils.Strings;
import lombok.Getter;

import java.util.Arrays;

/**
 * 应用env常量
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/4 11:29
 */
@Getter
public enum ApplicationEnvAttr {

    /**
     * 构建产物路径
     */
    BUNDLE_PATH("主机构建产物路径 (绝对路径/基于版本仓库的相对路径)"),

    /**
     * 产物传输路径
     */
    TRANSFER_PATH("产物传输绝对路径"),

    /**
     * 发布序列方式
     *
     * @see ReleaseSerialType
     */
    RELEASE_SERIAL("发布序列方式 (serial/parallel)"),

    /**
     * 构建序列号
     */
    BUILD_SEQ("构建序列号 (自增)") {
        @Override
        public ApplicationEnvDO getInitEnv() {
            ApplicationEnvDO buildSeq = new ApplicationEnvDO();
            buildSeq.setAttrKey(BUILD_SEQ.getKey());
            buildSeq.setAttrValue(Const.N_0 + Strings.EMPTY);
            buildSeq.setDescription(BUILD_SEQ.getDescription());
            buildSeq.setSystemEnv(Const.IS_SYSTEM);
            return buildSeq;
        }
    },

    ;

    /**
     * key
     */
    private final String key;

    /**
     * 描述
     */
    private final String description;

    ApplicationEnvAttr(String description) {
        this.description = description;
        this.key = this.name().toLowerCase();
    }

    public static ApplicationEnvAttr of(String key) {
        if (key == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(a -> a.key.equals(key))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取初始化的对象
     *
     * @return env
     */
    public ApplicationEnvDO getInitEnv() {
        throw new UnsupportedOperationException();
    }

}
