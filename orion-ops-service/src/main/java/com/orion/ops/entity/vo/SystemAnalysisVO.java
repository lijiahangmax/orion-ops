package com.orion.ops.entity.vo;

import com.orion.ops.entity.dto.SystemSpaceAnalysisDTO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

/**
 * 系统分析返回
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/16 22:25
 */
@Data
public class SystemAnalysisVO {

    /**
     * 已挂载秘钥数量
     */
    private Integer mountKeyCount;

    /**
     * 临时文件数量
     */
    private Integer tempFileCount;

    /**
     * 临时文件大小
     */
    private String tempFileSize;

    /**
     * 日志文件数量
     */
    private Integer logFileCount;

    /**
     * 日志文件大小
     */
    private String logFileSize;

    /**
     * 交换文件数量
     */
    private Integer swapFileCount;

    /**
     * 交换文件大小
     */
    private String swapFileSize;

    /**
     * 构建产物版本数
     */
    private Integer distVersionCount;

    /**
     * 构建产物大小
     */
    private String distFileSize;

    /**
     * 应用仓库版本数
     */
    private Integer vcsVersionCount;

    /**
     * 应用仓库大小
     */
    private String vcsVersionFileSize;

    /**
     * 黑名单数量
     */
    private Long blackIpListCount;

    /**
     * 白名单数量
     */
    private Long whiteIpListCount;

    /**
     * 文件清理阈值
     */
    private Integer fileCleanThreshold;

    /**
     * 自动清理文件
     */
    private Boolean autoCleanFile;

    static {
        TypeStore.STORE.register(SystemSpaceAnalysisDTO.class, SystemAnalysisVO.class, p -> {
            SystemAnalysisVO vo = new SystemAnalysisVO();
            vo.setTempFileCount(p.getTempFileCount());
            vo.setTempFileSize(p.getTempFileSize());
            vo.setLogFileCount(p.getLogFileCount());
            vo.setLogFileSize(p.getLogFileSize());
            vo.setSwapFileCount(p.getSwapFileCount());
            vo.setSwapFileSize(p.getSwapFileSize());
            vo.setDistVersionCount(p.getDistVersionCount());
            vo.setDistFileSize(p.getDistFileSize());
            vo.setVcsVersionCount(p.getVcsVersionCount());
            vo.setVcsVersionFileSize(p.getVcsVersionFileSize());
            return vo;
        });
    }

}
