package com.orion.ops.handler.build.handler;

import com.orion.ops.entity.domain.ApplicationBuildDO;
import com.orion.ops.handler.build.BuildStore;
import com.orion.ops.service.api.ApplicationVcsService;
import com.orion.spring.SpringHolder;
import com.orion.utils.io.Streams;
import com.orion.vcs.git.Gits;

/**
 * 构建执行操作-检出
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @see com.orion.ops.consts.app.ActionType#BUILD_CHECKOUT
 * @since 2021/12/6 10:54
 */
public class CheckoutBuildHandler extends AbstractBuildHandler {

    private static ApplicationVcsService applicationVcsService = SpringHolder.getBean(ApplicationVcsService.class);

    private Gits gits;

    public CheckoutBuildHandler(Long actionId, BuildStore store) {
        super(actionId, store);
    }

    @Override
    protected void handler() {
        ApplicationBuildDO buildRecord = store.getBuildRecord();
        // 打开git
        this.gits = applicationVcsService.openGit(buildRecord.getVcsId());
        // 查询分支
        String fullBranchName = buildRecord.getBranchName();
        String remote = fullBranchName.substring(0, fullBranchName.indexOf("/"));
        String branchName = fullBranchName.substring(fullBranchName.indexOf("/") + 1);
        String commitId = buildRecord.getCommitId();
        // 拼接日志
        String log = "*** 检出url: " + gits.getRemoteUrl() + "\t分支: " + fullBranchName + "\t提交: " + commitId + "\n";
        this.appendLog(log);
        // 执行操作
        gits.clean().pull(remote, branchName);
        gits.checkout(fullBranchName);
        gits.reset(commitId);
    }

    @Override
    public Integer getExitCode() {
        return null;
    }

    @Override
    public void close() {
        super.close();
        // 关闭gits
        Streams.close(gits);
    }

}