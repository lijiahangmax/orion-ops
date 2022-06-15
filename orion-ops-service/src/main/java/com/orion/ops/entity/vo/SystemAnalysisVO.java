package com.orion.ops.entity.vo;

import com.orion.ops.entity.dto.SystemSpaceAnalysisDTO;
import com.orion.utils.convert.TypeStore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 系统分析响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/16 22:25
 */
@Data
@ApiModel(value = "系统分析响应")
public class SystemAnalysisVO {

    @ApiModelProperty(value = "已挂载秘钥数量")
    private Integer mountKeyCount;

    @ApiModelProperty(value = "临时文件数量")
    private Integer tempFileCount;

    @ApiModelProperty(value = "临时文件大小")
    private String tempFileSize;

    @ApiModelProperty(value = "日志文件数量")
    private Integer logFileCount;

    @ApiModelProperty(value = "日志文件大小")
    private String logFileSize;

    @ApiModelProperty(value = "交换文件数量")
    private Integer swapFileCount;

    @ApiModelProperty(value = "交换文件大小")
    private String swapFileSize;

    @ApiModelProperty(value = "构建产物版本数")
    private Integer distVersionCount;

    @ApiModelProperty(value = "构建产物大小")
    private String distFileSize;

    @ApiModelProperty(value = "应用仓库版本数")
    private Integer vcsVersionCount;

    @ApiModelProperty(value = "应用仓库大小")
    private String vcsVersionFileSize;

    @ApiModelProperty(value = "黑名单数量")
    private Long blackIpListCount;

    @ApiModelProperty(value = "白名单数量")
    private Long whiteIpListCount;

    @ApiModelProperty(value = "文件清理阈值")
    private Integer fileCleanThreshold;

    @ApiModelProperty(value = "自动清理文件")
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
