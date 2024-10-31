/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.runner;

import cn.orionsec.kit.lang.utils.io.Files1;
import cn.orionsec.ops.constant.app.RepositoryStatus;
import cn.orionsec.ops.dao.ApplicationRepositoryDAO;
import cn.orionsec.ops.entity.domain.ApplicationRepositoryDO;
import cn.orionsec.ops.utils.Utils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
