package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.FileTailListDO;
import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import lombok.Data;

import java.util.Date;

/**
 * 文件tail 返回
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/20 19:17
 */
@Data
public class FileTailVO {

    /**
     * token
     */
    private String token;

    /**
     * id
     */
    private Long id;

    /**
     * name
     */
    private String name;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 机器名称
     */
    private String machineName;

    /**
     * 机器host
     */
    private String machineHost;

    /**
     * 文件
     */
    private String path;

    /**
     * offset
     *
     * @see com.orion.ops.consts.Const#TAIL_OFFSET_LINE
     */
    private Integer offset;

    /**
     * 编码集
     *
     * @see com.orion.ops.consts.Const#UTF_8
     */
    private String charset;

    /**
     * tail 命令
     */
    private String command;

    /**
     * 宿主机文件追踪类型 tacker/tail
     *
     * @see com.orion.ops.consts.tail.FileTailMode
     */
    private String tailMode;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新时间
     */
    private String updateTimeAgo;

    static {
        TypeStore.STORE.register(FileTailListDO.class, FileTailVO.class, p -> {
            FileTailVO vo = new FileTailVO();
            vo.setId(p.getId());
            vo.setName(p.getAliasName());
            vo.setMachineId(p.getMachineId());
            vo.setPath(p.getFilePath());
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
