package com.orion.ops.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.consts.sftp.SftpTransferStatus;
import com.orion.ops.dao.FileTransferLogDAO;
import com.orion.ops.entity.domain.FileTransferLogDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 重置传输状态
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/29 18:38
 */
@Component
@Order(140)
@Slf4j
public class CleanTransferStatusRunner implements CommandLineRunner {

    @Resource
    private FileTransferLogDAO fileTransferLogDAO;

    @Override
    public void run(String... args) {
        log.info("重置传输状态-开始");
        LambdaQueryWrapper<FileTransferLogDO> wrapper = new LambdaQueryWrapper<FileTransferLogDO>()
                .eq(FileTransferLogDO::getTransferStatus, SftpTransferStatus.RUNNABLE.getStatus());
        fileTransferLogDAO.selectList(wrapper).forEach(c -> {
            FileTransferLogDO update = new FileTransferLogDO();
            update.setId(c.getId());
            update.setTransferStatus(SftpTransferStatus.PAUSE.getStatus());
            fileTransferLogDAO.updateById(update);
            log.info("重置传输状态-重置 {}", c.getId());
        });
        log.info("重置传输状态-结束");
    }

}
