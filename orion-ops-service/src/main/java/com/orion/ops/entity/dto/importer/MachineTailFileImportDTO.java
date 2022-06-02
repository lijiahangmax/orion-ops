package com.orion.ops.entity.dto.importer;

import com.orion.office.excel.annotation.ImportField;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.tail.FileTailMode;
import com.orion.ops.entity.domain.FileTailListDO;
import com.orion.ops.entity.vo.DataImportCheckRowVO;
import com.orion.utils.convert.TypeStore;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 机器代理导入
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/31 9:54
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MachineTailFileImportDTO extends BaseDataImportDTO {

    /**
     * 机器标识
     */
    @ImportField(index = 1)
    private String machineTag;

    /**
     * 别名
     */
    @ImportField(index = 2)
    private String name;

    /**
     * 文件路径
     */
    @ImportField(index = 3)
    private String path;

    /**
     * 文件编码
     */
    @ImportField(index = 4)
    private String charset;

    /**
     * 尾部偏移行
     */
    @ImportField(index = 5)
    private Integer offset;

    /**
     * 执行命令
     */
    @ImportField(index = 6)
    private String command;

    /**
     * 机器id
     */
    private Long machineId;

    static {
        TypeStore.STORE.register(MachineTailFileImportDTO.class, DataImportCheckRowVO.class, p -> {
            DataImportCheckRowVO vo = new DataImportCheckRowVO();
            vo.setSymbol(p.name);
            vo.setIllegalMessage(p.getIllegalMessage());
            vo.setId(p.getId());
            return vo;
        });
        TypeStore.STORE.register(MachineTailFileImportDTO.class, FileTailListDO.class, p -> {
            FileTailListDO d = new FileTailListDO();
            d.setId(p.getId());
            d.setMachineId(p.machineId);
            d.setAliasName(p.name);
            d.setFilePath(p.path);
            d.setFileCharset(p.charset);
            d.setFileOffset(p.offset);
            d.setTailCommand(p.command);
            if (Const.HOST_MACHINE_ID.equals(p.machineId)) {
                d.setTailMode(FileTailMode.TRACKER.getMode());
            } else {
                d.setTailMode(FileTailMode.TAIL.getMode());
            }
            return d;
        });
    }

}
