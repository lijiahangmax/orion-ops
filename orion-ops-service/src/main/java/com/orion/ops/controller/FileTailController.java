package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.event.EventType;
import com.orion.ops.consts.tail.FileTailType;
import com.orion.ops.entity.request.FileTailRequest;
import com.orion.ops.entity.vo.FileTailConfigVO;
import com.orion.ops.entity.vo.FileTailVO;
import com.orion.ops.service.api.FileTailService;
import com.orion.ops.utils.Valid;
import com.orion.utils.Charsets;
import com.orion.utils.Strings;
import com.orion.utils.io.Files1;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 文件tail api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/8/1 23:31
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/file-tail")
public class FileTailController {

    @Resource
    private FileTailService fileTailService;

    /**
     * 获取 tail 文件 token 检查文件存在以及权限
     */
    @RequestMapping("/token")
    public FileTailVO getToken(@RequestBody FileTailRequest request) {
        FileTailType type = Valid.notNull(FileTailType.of(request.getType()));
        Long relId = Valid.notNull(request.getRelId());
        return fileTailService.getTailToken(type, relId);
    }

    /**
     * 添加 tail 文件
     */
    @RequestMapping("/add")
    @EventLog(EventType.ADD_TAIL_FILE)
    public Long addTailFile(@RequestBody FileTailRequest request) {
        Valid.notBlank(request.getName());
        Valid.notNull(request.getMachineId());
        Valid.notBlank(request.getPath());
        Valid.notNull(request.getOffset());
        Valid.notNull(request.getCharset());
        Valid.isTrue(Files1.isPath(request.getPath()));
        Valid.isTrue(Charsets.isSupported(request.getCharset()));
        return fileTailService.insertTailFile(request);
    }

    /**
     * 修改 tail 文件
     */
    @RequestMapping("/update")
    @EventLog(EventType.UPDATE_TAIL_FILE)
    public Integer updateTailFile(@RequestBody FileTailRequest request) {
        Valid.notNull(request.getId());
        if (!Strings.isBlank(request.getPath())) {
            Valid.isTrue(Files1.isPath(request.getPath()));
        }
        if (!Strings.isBlank(request.getCharset())) {
            Valid.isTrue(Charsets.isSupported(request.getCharset()));
        }
        return fileTailService.updateTailFile(request);
    }

    /**
     * 删除 tail 文件
     */
    @RequestMapping("/delete")
    @EventLog(EventType.DELETE_TAIL_FILE)
    public Integer deleteTailFile(@RequestBody FileTailRequest request) {
        Long id = Valid.notNull(request.getId());
        return fileTailService.deleteTailFile(id);
    }

    /**
     * tail 文件列表
     */
    @RequestMapping("/list")
    public DataGrid<FileTailVO> tailFileList(@RequestBody FileTailRequest request) {
        return fileTailService.tailFileList(request);
    }

    /**
     * tail 文件详情
     */
    @RequestMapping("/detail")
    public FileTailVO tailFileDetail(@RequestBody FileTailRequest request) {
        Long id = Valid.notNull(request.getId());
        return fileTailService.tailFileDetail(id);
    }

    /**
     * 获取机器默认配置
     */
    @RequestMapping("/config")
    public FileTailConfigVO getMachineConfig(@RequestBody FileTailRequest request) {
        Long machineId = Valid.notNull(request.getMachineId());
        return fileTailService.getMachineConfig(machineId);
    }

    /**
     * 写入命令
     */
    @RequestMapping("/write")
    public void write(@RequestBody FileTailRequest request) {
        String token = Valid.notBlank(request.getToken());
        String command = Valid.notEmpty(request.getCommand());
        fileTailService.writeCommand(token, command);
    }

}
