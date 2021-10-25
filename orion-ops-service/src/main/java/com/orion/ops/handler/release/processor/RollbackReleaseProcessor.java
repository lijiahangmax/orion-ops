package com.orion.ops.handler.release.processor;

import com.orion.ops.consts.Const;
import com.orion.ops.handler.release.action.IReleaseActionHandler;
import com.orion.ops.handler.release.action.ReleaseConnectActionHandler;
import com.orion.ops.handler.release.action.ReleaseTargetStageHandler;
import com.orion.ops.handler.release.action.ReleaseTransferActionHandler;
import com.orion.ops.handler.release.hint.ReleaseHint;
import com.orion.utils.Strings;
import com.orion.utils.time.Dates;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 回滚上线单 处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/30 19:04
 */
@Slf4j
public class RollbackReleaseProcessor extends AbstractReleaseProcessor {

    public RollbackReleaseProcessor(ReleaseHint hint, List<IReleaseActionHandler> hostActions, List<ReleaseTargetStageHandler> targetStages) {
        super(hint, hostActions, targetStages);
    }

    @Override
    protected boolean runHostAction() {
        try {
            for (IReleaseActionHandler action : hostActions) {
                if (action instanceof ReleaseConnectActionHandler
                        || action instanceof ReleaseTransferActionHandler) {
                    // 回滚只执行连接和传输操作
                    action.handle();
                    action.close();
                } else {
                    action.skip();
                }
            }
            return true;
        } catch (Exception ex) {
            // skip
            this.exception = ex;
            for (IReleaseActionHandler action : hostActions) {
                action.skip();
            }
            return false;
        }
    }

    @Override
    protected void init() {
        super.init();
        // 打印日志
        StringBuilder sb = new StringBuilder()
                .append("# 开始回滚上线单宿主机操作\n")
                .append("id: ").append(releaseId).append(Const.LF)
                .append("rollbackId: ").append(hint.getRollbackReleaseId()).append(Const.LF)
                .append("执行人: ").append(hint.getReleaseUserId()).append(Const.SPACE_4)
                .append(hint.getReleaseUserName()).append(Const.LF)
                .append("应用: ").append(hint.getAppName()).append(Const.SPACE_4)
                .append(hint.getProfileName()).append(Const.LF)
                .append("标题: ").append(hint.getTitle()).append(Const.LF);
        if (!Strings.isBlank(hint.getDescription())) {
            sb.append("描述: ").append(hint.getDescription()).append(Const.LF);
        }
        sb.append("开始时间: ").append(Dates.format(startTime, Dates.YMD_HMS)).append(Const.LF);
        this.appendLog(sb.toString());
    }

}
