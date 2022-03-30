package com.orion.ops.handler.app.action;

import com.orion.ops.consts.MessageConst;
import com.orion.ops.entity.domain.ApplicationVcsDO;
import com.orion.ops.service.api.ApplicationVcsService;
import com.orion.spring.SpringHolder;
import com.orion.utils.Exceptions;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;

/**
 * 执行操作-检出
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @see com.orion.ops.consts.app.ActionType#BUILD_CHECKOUT
 * @since 2022/2/11 15:57
 */
public class CheckoutActionHandler extends AbstractActionHandler {

    private static ApplicationVcsService applicationVcsService = SpringHolder.getBean(ApplicationVcsService.class);

    private Git git;

    public CheckoutActionHandler(Long actionId, MachineActionStore store) {
        super(actionId, store);
    }

    @Override
    protected void handler() {
        ApplicationVcsDO vcs = applicationVcsService.selectById(store.getVcsId());
        // 查询分支
        String fullBranchName = store.getBranchName();
        String remote = fullBranchName.substring(0, fullBranchName.indexOf("/"));
        String branchName = fullBranchName.substring(fullBranchName.indexOf("/") + 1);
        String commitId = store.getCommitId();
        String vcsClonePath = store.getVcsClonePath();
        Files1.delete(vcsClonePath);
        // 拼接日志
        String log = "*** 检出url: " + vcs.getVscUrl() + "\n" +
                "*** 分支: " + fullBranchName + "\n" +
                "*** commit: " + commitId + "\n" +
                "*** 检出目录: " + vcsClonePath + "\n" +
                "*** 检出中...\n";
        this.appendLog(log);
        // clone
        try {
            CloneCommand clone = Git.cloneRepository()
                    .setURI(vcs.getVscUrl())
                    .setDirectory(new File(vcsClonePath))
                    .setRemote(remote)
                    .setBranch(branchName);
            // 设置密码
            String[] pair = applicationVcsService.getVcsUsernamePassword(vcs);
            String username = pair[0];
            String password = pair[1];
            if (username != null) {
                clone.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
            }
            this.git = clone.call();
            this.appendLog("*** 检出完成\n");
        } catch (Exception e) {
            throw Exceptions.vcs(MessageConst.CHECKOUT_ERROR, e);
        }
        // 已停止则关闭
        if (terminated) {
            return;
        }
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
    public void terminated() {
        super.terminated();
        // 关闭git
        Streams.close(git);
    }

    @Override
    public void close() {
        super.close();
        // 关闭git
        Streams.close(git);
    }

}
