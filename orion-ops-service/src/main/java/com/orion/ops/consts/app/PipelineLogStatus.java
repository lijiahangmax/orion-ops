package com.orion.ops.consts.app;

import com.orion.ops.utils.Valid;
import com.orion.utils.Strings;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 流水线日志状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/14 15:57
 */
@AllArgsConstructor
@Getter
public enum PipelineLogStatus {

    /**
     * 创建
     */
    CREATE(10, "已创建应用 <sb>{}</sb> 构建任务 <sb>#{}</sb>",
            "已创建应用 <sb>{}</sb> 发布任务 <sb>#{}</sb>"),

    /**
     * 开始执行
     */
    EXEC(20, "开始执行应用 <sb>{}</sb> 构建任务",
            "开始执行应用 <sb>{}</sb> 发布任务"),

    /**
     * 执行成功
     */
    SUCCESS(30, "应用 <sb>{}</sb> 构建任务执行成功",
            "应用 <sb>{}</sb> 发布任务执行成功"),

    /**
     * 执行失败
     */
    FAILURE(40, "应用 <sb>{}</sb> 构建任务执行失败",
            "应用 <sb>{}</sb> 发布任务执行失败"),

    /**
     * 停止执行
     */
    TERMINATED(50, "应用 <sb>{}</sb> 构建任务已停止执行",
            "应用 <sb>{}</sb> 发布任务已停止执行"),

    /**
     * 跳过执行
     */
    SKIP(60, "应用 <sb>{}</sb> 构建任务已跳过执行",
            "应用 <sb>{}</sb> 发布任务已跳过执行"),

    ;

    /**
     * 状态
     */
    private final Integer status;

    /**
     * 构建模板
     */
    private final String buildTemplate;

    /**
     * 发布模板
     */
    private final String releaseTemplate;

    /**
     * 格式日志
     *
     * @param stage stage
     * @param args  参数
     * @return log
     */
    public String format(StageType stage, Object... args) {
        Valid.notNull(stage);
        if (StageType.BUILD.equals(stage)) {
            return Strings.format(buildTemplate, args);
        } else if (StageType.RELEASE.equals(stage)) {
            return Strings.format(releaseTemplate, args);
        } else {
            return Strings.EMPTY;
        }
    }

}
