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

import cn.orionsec.ops.constant.sftp.SftpTransferStatus;
import cn.orionsec.ops.constant.sftp.SftpTransferType;
import cn.orionsec.ops.dao.FileTransferLogDAO;
import cn.orionsec.ops.entity.domain.FileTransferLogDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 重置传输状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/29 18:38
 */
@Component
@Order(2100)
@Slf4j
public class SftpTransferStatusCleanRunner implements CommandLineRunner {

    @Resource
    private FileTransferLogDAO fileTransferLogDAO;

    @Override
    public void run(String... args) {
        log.info("重置传输状态-开始");
        // 更新可打包传输状态
        LambdaQueryWrapper<FileTransferLogDO> packageWrapper = new LambdaQueryWrapper<FileTransferLogDO>()
                .eq(FileTransferLogDO::getTransferType, SftpTransferType.PACKAGE.getType())
                .in(FileTransferLogDO::getTransferStatus, SftpTransferStatus.WAIT.getStatus(), SftpTransferStatus.RUNNABLE.getStatus());
        FileTransferLogDO updatePackage = new FileTransferLogDO();
        updatePackage.setTransferStatus(SftpTransferStatus.CANCEL.getStatus());
        updatePackage.setUpdateTime(new Date());
        fileTransferLogDAO.update(updatePackage, packageWrapper);

        // 更新可恢复传输状态
        LambdaQueryWrapper<FileTransferLogDO> resumeWrapper = new LambdaQueryWrapper<FileTransferLogDO>()
                .ne(FileTransferLogDO::getTransferType, SftpTransferType.PACKAGE.getType())
                .in(FileTransferLogDO::getTransferStatus, SftpTransferStatus.WAIT.getStatus(), SftpTransferStatus.RUNNABLE.getStatus());
        FileTransferLogDO updateResume = new FileTransferLogDO();
        updateResume.setTransferStatus(SftpTransferStatus.PAUSE.getStatus());
        updateResume.setUpdateTime(new Date());
        fileTransferLogDAO.update(updateResume, resumeWrapper);
        log.info("重置传输状态-结束");
    }

}
