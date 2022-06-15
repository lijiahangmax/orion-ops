package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.FileTailListDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.io.Files1;
import com.orion.utils.time.Dates;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 文件tail响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/20 19:17
 */
@Data
@ApiModel(value = "文件tail响应")
public class FileTailVO {

    @ApiModelProperty(value = "token")
    private String token;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    @ApiModelProperty(value = "机器名称")
    private String machineName;

    @ApiModelProperty(value = "机器host")
    private String machineHost;

    /**
     * @see com.orion.ops.consts.Const#ENABLE
     * @see com.orion.ops.consts.Const#DISABLE
     */
    @ApiModelProperty(value = "机器状态 1有效 2无效")
    private Integer machineStatus;

    @ApiModelProperty(value = "文件路径")
    private String path;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    /**
     * @see com.orion.ops.consts.Const#TAIL_OFFSET_LINE
     */
    @ApiModelProperty(value = "偏移量")
    private Integer offset;

    /**
     * @see com.orion.ops.consts.Const#UTF_8
     */
    @ApiModelProperty(value = "编码集")
    private String charset;

    @ApiModelProperty(value = "命令")
    private String command;

    /**
     * @see com.orion.ops.consts.tail.FileTailMode
     */
    @ApiModelProperty(value = "宿主机文件追踪类型 tracker/tail")
    private String tailMode;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "更新时间")
    private String updateTimeAgo;

    static {
        TypeStore.STORE.register(FileTailListDO.class, FileTailVO.class, p -> {
            FileTailVO vo = new FileTailVO();
            vo.setId(p.getId());
            vo.setName(p.getAliasName());
            vo.setMachineId(p.getMachineId());
            vo.setPath(p.getFilePath());
            vo.setFileName(Files1.getFileName(p.getFilePath()));
            vo.setOffset(p.getFileOffset());
            vo.setCharset(p.getFileCharset());
            vo.setCommand(p.getTailCommand());
            vo.setTailMode(p.getTailMode());
            vo.setUpdateTime(p.getUpdateTime());
            vo.setUpdateTimeAgo(Dates.ago(p.getUpdateTime()));
            return vo;
        });
    }

}
