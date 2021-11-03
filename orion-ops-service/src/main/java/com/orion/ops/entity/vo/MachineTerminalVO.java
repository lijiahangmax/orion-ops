package com.orion.ops.entity.vo;

import com.orion.ops.entity.domain.MachineTerminalDO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;

import java.util.Date;

/**
 * 终端配置VO
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/2 21:02
 */
@Data
public class MachineTerminalVO {

    /**
     * id
     */
    private Long id;

    /**
     * 机器id
     */
    private Long machineId;

    /**
     * 终端类型
     *
     * @see com.orion.remote.TerminalType
     */
    private String terminalType;

    /**
     * 背景色
     *
     * @see com.orion.ops.consts.terminal.TerminalConst#BACKGROUND_COLOR
     */
    private String backgroundColor;

    /**
     * 字体颜色
     *
     * @see com.orion.ops.consts.terminal.TerminalConst#FONT_COLOR
     */
    private String fontColor;

    /**
     * 字体大小
     *
     * @see com.orion.ops.consts.terminal.TerminalConst#FONT_SIZE
     */
    private Integer fontSize;

    /**
     * 是否开启url link 1开启 2关闭
     *
     * @see com.orion.ops.consts.Const#ENABLE
     * @see com.orion.ops.consts.Const#DISABLE
     */
    private Integer enableWebLink;

    /**
     * 是否开启webGL加速 1开启 2关闭
     *
     * @see com.orion.ops.consts.Const#ENABLE
     * @see com.orion.ops.consts.Const#DISABLE
     */
    private Integer enableWebGL;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    static {
        TypeStore.STORE.register(MachineTerminalDO.class, MachineTerminalVO.class, p -> {
            MachineTerminalVO vo = new MachineTerminalVO();
            vo.setId(p.getId());
            vo.setMachineId(p.getMachineId());
            vo.setTerminalType(p.getTerminalType());
            vo.setBackgroundColor(p.getBackgroundColor());
            vo.setFontColor(p.getFontColor());
            vo.setFontSize(p.getFontSize());
            vo.setEnableWebLink(p.getEnableWebLink());
            vo.setEnableWebGL(p.getEnableWebGL());
            vo.setCreateTime(p.getCreateTime());
            vo.setUpdateTime(p.getUpdateTime());
            return vo;
        });
    }

}
