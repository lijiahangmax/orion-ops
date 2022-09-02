package com.orion.ops.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.utils.io.Files1;
import com.orion.ops.constant.app.RepositoryStatus;
import com.orion.ops.dao.ApplicationRepositoryDAO;
import com.orion.ops.entity.domain.ApplicationRepositoryDO;
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
public class AppRepositoryStatusCleanRunner implements CommandLineRunner {

    @Resource
    private ApplicationRepositoryDAO applicationRepositoryDAO;

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
        LambdaQueryWrapper<ApplicationRepositoryDO> wrapper = new LambdaQueryWrapper<ApplicationRepositoryDO>()
                .eq(ApplicationRepositoryDO::getRepoStatus, RepositoryStatus.INITIALIZING.getStatus());
        List<ApplicationRepositoryDO> repoList = applicationRepositoryDAO.selectList(wrapper);
        for (ApplicationRepositoryDO repo : repoList) {
            Long id = repo.getId();
            // 更新状态
            ApplicationRepositoryDO update = new ApplicationRepositoryDO();
            update.setId(id);
            update.setRepoStatus(RepositoryStatus.UNINITIALIZED.getStatus());
            applicationRepositoryDAO.updateById(update);
            // 删除文件夹
            File clonePath = new File(Utils.getRepositoryEventDir(id));
            Files1.delete(clonePath);
            log.info("重置版本仓库状态-重置 id: {}, clonePath: {}", id, clonePath);
        }
    }

    /**
     * 检查已初始化的数据
     */
    private void checkFinished() {
        LambdaQueryWrapper<ApplicationRepositoryDO> wrapper = new LambdaQueryWrapper<ApplicationRepositoryDO>()
                .eq(ApplicationRepositoryDO::getRepoStatus, RepositoryStatus.OK.getStatus());
        List<ApplicationRepositoryDO> repoList = applicationRepositoryDAO.selectList(wrapper);
        for (ApplicationRepositoryDO repo : repoList) {
            // 检查是否存在
            Long id = repo.getId();
            File clonePath = new File(Utils.getRepositoryEventDir(id));
            if (Files1.isDirectory(clonePath)) {
                continue;
            }
            // 更新状态
            ApplicationRepositoryDO update = new ApplicationRepositoryDO();
            update.setId(id);
            update.setRepoStatus(RepositoryStatus.UNINITIALIZED.getStatus());
            applicationRepositoryDAO.updateById(update);
            log.info("重置版本仓库状态-重置 id: {}, clonePath: {}", id, clonePath);
        }
    }

}
