package com.orion.ops.controller;

import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.IgnoreWrapper;
import com.orion.ops.annotation.RestWrapper;
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
     * tail文件 检查文件存在以及权限
     */
    @RequestMapping("/token")
    @IgnoreWrapper
    public HttpWrapper<FileTailVO> getToken(@RequestBody FileTailRequest request) {
        Valid.notNull(FileTailType.of(request.getType()));
        Valid.notNull(request.getRelId());
        return fileTailService.getTailToken(request);
    }

    /**
     * 添加tail文件
     */
    @RequestMapping("/add")
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
     * 修改tail文件
     */
    @RequestMapping("/update")
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
     * tail文件列表
     */
    @RequestMapping("/list")
    public DataGrid<FileTailVO> tailFileList(@RequestBody FileTailRequest request) {
        return fileTailService.tailFileList(request);
    }

    /**
     * 获取机器默认配置
     */
    @RequestMapping("/config")
    public FileTailConfigVO getMachineConfig(@RequestBody FileTailRequest request) {
        Long machineId = Valid.notNull(request.getMachineId());
        return fileTailService.getMachineConfig(machineId);
    }

}
