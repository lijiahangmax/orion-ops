package com.orion.ops.handler.release.action;

import com.orion.ops.handler.release.hint.ReleaseActionHint;
import com.orion.ops.handler.release.hint.ReleaseHint;
import com.orion.utils.io.Streams;
import com.orion.vcs.git.Gits;

import java.io.File;

/**
 * 宿主机检出代码处理器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @see com.orion.ops.consts.app.ActionType#CHECKOUT
 * @since 2021/7/15 18:49
 */
public class ReleaseCheckoutActionHandler extends AbstractReleaseHostActionHandler {

    private Gits git;

    public ReleaseCheckoutActionHandler(ReleaseHint hint, ReleaseActionHint action) {
        super(hint, action);
    }

    @Override
    protected void handleAction() {
        String vcsLocalPath = hint.getVcsLocalPath();
        String branchName = hint.getBranchName();
        String commitId = hint.getCommitId();
        this.printLog("开始检出代码 path: {}, branch: {}, commitId: {}", vcsLocalPath, branchName, commitId);
        this.git = Gits.of(new File(vcsLocalPath));
        git.clean().pull()
                .checkout(branchName)
                .reset(commitId);
    }

    @Override
    public void close() {
        super.close();
        Streams.close(git);
    }

}
