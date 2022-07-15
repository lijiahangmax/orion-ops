package com.orion.ops.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.utils.io.Files1;
import com.orion.ops.consts.app.VcsStatus;
import com.orion.ops.dao.ApplicationVcsDAO;
import com.orion.ops.entity.domain.ApplicationVcsDO;
import com.orion.ops.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * 重置版本仓库状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/29 20:16
 */
@Component
@Order(2500)
@Slf4j
public class CleanVcsStatusRunner implements CommandLineRunner {

    @Resource
    private ApplicationVcsDAO applicationVcsDAO;

    @Override
    public void run(String... args) {
        log.info("重置版本仓库状态-开始");
        // 清空初始化中的数据
        this.cleanInitializing();
        // 检查已初始化的数据
        this.checkFinished();
        log.info("重置版本仓库状态-结束");
    }

    /**
     * 清空初始化中的数据
     */
    private void cleanInitializing() {
        LambdaQueryWrapper<ApplicationVcsDO> wrapper = new LambdaQueryWrapper<ApplicationVcsDO>()
                .eq(ApplicationVcsDO::getVcsStatus, VcsStatus.INITIALIZING.getStatus());
        List<ApplicationVcsDO> vcsList = applicationVcsDAO.selectList(wrapper);
        for (ApplicationVcsDO vcs : vcsList) {
            Long id = vcs.getId();
            // 更新状态
            ApplicationVcsDO update = new ApplicationVcsDO();
            update.setId(id);
            update.setVcsStatus(VcsStatus.UNINITIALIZED.getStatus());
            applicationVcsDAO.updateById(update);
            // 删除文件夹
            File clonePath = new File(Utils.getVcsEventDir(id));
            Files1.delete(clonePath);
            log.info("重置版本仓库状态-重置 id: {}, clonePath: {}", id, clonePath);
        }
    }

    /**
     * 检查已初始化的数据
     */
    private void checkFinished() {
        LambdaQueryWrapper<ApplicationVcsDO> wrapper = new LambdaQueryWrapper<ApplicationVcsDO>()
                .eq(ApplicationVcsDO::getVcsStatus, VcsStatus.OK.getStatus());
        List<ApplicationVcsDO> vcsList = applicationVcsDAO.selectList(wrapper);
        for (ApplicationVcsDO vcs : vcsList) {
            // 检查是否存在
            Long id = vcs.getId();
            File clonePath = new File(Utils.getVcsEventDir(id));
            if (Files1.isDirectory(clonePath)) {
                continue;
            }
            // 更新状态
            ApplicationVcsDO update = new ApplicationVcsDO();
            update.setId(id);
            update.setVcsStatus(VcsStatus.UNINITIALIZED.getStatus());
            applicationVcsDAO.updateById(update);
            log.info("重置版本仓库状态-重置 id: {}, clonePath: {}", id, clonePath);
        }
    }

}
