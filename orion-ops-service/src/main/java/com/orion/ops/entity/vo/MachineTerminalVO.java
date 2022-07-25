package com.orion.ops.entity.vo;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.ops.entity.domain.MachineTerminalDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 终端终端配置响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/2 21:02
 */
@Data
@ApiModel(value = "终端终端配置响应")
public class MachineTerminalVO {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "机器id")
    private Long machineId;

    /**
     * @see com.orion.net.remote.TerminalType
     */
    @ApiModelProperty(value = "终端类型")
    private String terminalType;

    /**
     * @see com.orion.ops.constant.terminal.TerminalConst#BACKGROUND_COLOR
     */
    @ApiModelProperty(value = "背景色")
    private String backgroundColor;

    /**
     * @see com.orion.ops.constant.terminal.TerminalConst#FONT_COLOR
     */
    @ApiModelProperty(value = "字体颜色")
    private String fontColor;

    /**
     * @see com.orion.ops.constant.terminal.TerminalConst#FONT_SIZE
     */
    @ApiModelProperty(value = "字体大小")
    private Integer fontSize;

    /**
     * @see com.orion.ops.constant.terminal.TerminalConst#FONT_FAMILY
     */
    @ApiModelProperty(value = "字体名称")
    private String fontFamily;

    /**
     * @see com.orion.ops.constant.Const#ENABLE
     * @see com.orion.ops.constant.Const#DISABLE
     */
    @ApiModelProperty(value = "是否开启url link 1开启 2关闭")
    private Integer enableWebLink;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
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
            vo.setFontFamily(p.getFontFamily());
            vo.setEnableWebLink(p.getEnableWebLink());
            vo.setCreateTime(p.getCreateTime());
            vo.setUpdateTime(p.getUpdateTime());
            return vo;
        });
    }

}
