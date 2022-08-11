package com.orion.ops.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.constant.sftp.SftpTransferStatus;
import com.orion.ops.constant.sftp.SftpTransferType;
import com.orion.ops.dao.FileTransferLogDAO;
import com.orion.ops.entity.domain.FileTransferLogDO;
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
public class CleanTransferStatusRunner implements CommandLineRunner {

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
