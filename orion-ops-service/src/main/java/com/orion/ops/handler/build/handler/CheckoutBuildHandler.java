package com.orion.ops.handler.build.handler;

import com.orion.ops.consts.MessageConst;
import com.orion.ops.dao.ApplicationVcsDAO;
import com.orion.ops.entity.domain.ApplicationBuildDO;
import com.orion.ops.entity.domain.ApplicationVcsDO;
import com.orion.ops.handler.build.BuildStore;
import com.orion.ops.utils.ValueMix;
import com.orion.spring.SpringHolder;
import com.orion.utils.Exceptions;
import com.orion.utils.io.Streams;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;

/**
 * 构建执行操作-检出
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @see com.orion.ops.consts.app.ActionType#BUILD_CHECKOUT
 * @since 2021/12/6 10:54
 */
public class CheckoutBuildHandler extends AbstractBuildHandler {

    private static ApplicationVcsDAO applicationVcsDAO = SpringHolder.getBean(ApplicationVcsDAO.class);

    private Git git;

    public CheckoutBuildHandler(Long actionId, BuildStore store) {
        super(actionId, store);
    }

    @Override
    protected void handler() {
        ApplicationBuildDO buildRecord = store.getBuildRecord();
        ApplicationVcsDO vcs = applicationVcsDAO.selectById(buildRecord.getVcsId());
        // 查询分支
        String fullBranchName = buildRecord.getBranchName();
        String remote = fullBranchName.substring(0, fullBranchName.indexOf("/"));
        String branchName = fullBranchName.substring(fullBranchName.indexOf("/") + 1);
        String commitId = buildRecord.getCommitId();
        // 拼接日志
        String log = "*** 检出url: " + vcs.getVscUrl() + "\tremote: " + remote + "\t分支: " + branchName + "\t提交: " + commitId + "\n" +
                "*** 检出目录: " + store.getVcsClonePath() + "\n" +
                "*** 检出中... ...\n";
        this.appendLog(log);
        // clone
        try {
            CloneCommand clone = Git.cloneRepository()
                    .setURI(vcs.getVscUrl())
                    .setDirectory(new File(store.getVcsClonePath()))
                    .setRemote(remote)
                    .setBranch(branchName);
            // 设置密码
            if (vcs.getVscUsername() != null) {
                clone.setCredentialsProvider(new UsernamePasswordCredentialsProvider(vcs.getVscUsername(), ValueMix.decrypt(vcs.getVcsPassword())));
            }
            this.git = clone.call();
        } catch (Exception e) {
            throw Exceptions.vcs(MessageConst.CHECKOUT_ERROR, e);
        }
        this.appendLog("*** 检出完成\n");
        // reset
        try {
            git.reset().setMode(ResetCommand.ResetType.HARD)
                    .setRef(commitId)
                    .call();
        } catch (Exception e) {
            throw Exceptions.vcs(MessageConst.RESET_ERROR, e);
        }
    }

    @Override
    public Integer getExitCode() {
        return null;
    }

    @Override
    public void close() {
        super.close();
        // 关闭git
        Streams.close(git);
    }

}