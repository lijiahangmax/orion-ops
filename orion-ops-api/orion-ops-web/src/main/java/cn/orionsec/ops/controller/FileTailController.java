/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.orionsec.ops.controller;

import cn.orionsec.kit.lang.define.wrapper.DataGrid;
import cn.orionsec.kit.lang.define.wrapper.HttpWrapper;
import cn.orionsec.kit.lang.utils.Charsets;
import cn.orionsec.kit.lang.utils.Strings;
import cn.orionsec.kit.lang.utils.collect.Lists;
import cn.orionsec.kit.lang.utils.io.Files1;
import cn.orionsec.kit.lang.utils.io.Streams;
import cn.orionsec.kit.web.servlet.web.Servlets;
import cn.orionsec.ops.annotation.DemoDisableApi;
import cn.orionsec.ops.annotation.EventLog;
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.constant.system.SystemEnvAttr;
import cn.orionsec.ops.constant.tail.FileTailType;
import cn.orionsec.ops.entity.request.file.FileTailRequest;
import cn.orionsec.ops.entity.vo.tail.FileTailConfigVO;
import cn.orionsec.ops.entity.vo.tail.FileTailVO;
import cn.orionsec.ops.service.api.FileTailService;
import cn.orionsec.ops.utils.Utils;
import cn.orionsec.ops.utils.Valid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 文件tail api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/8/1 23:31
 */
@Api(tags = "日志文件tail")
@RestController
@RestWrapper
@RequestMapping("/orion/api/file-tail")
public class FileTailController {

    @Resource
    private FileTailService fileTailService;

    @PostMapping("/token")
    @ApiOperation(value = "检查并获取日志文件token")
    public FileTailVO getTailToken(@RequestBody FileTailRequest request) {
        FileTailType type = Valid.notNull(FileTailType.of(request.getType()));
        Long relId = Valid.notNull(request.getRelId());
        return fileTailService.getTailToken(type, relId);
    }

    @DemoDisableApi
    @PostMapping("/add")
    @ApiOperation(value = "添加日志文件")
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

    @DemoDisableApi
    @PostMapping("/update")
    @ApiOperation(value = "修改日志文件")
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

    @PostMapping("/upload")
    @ApiOperation(value = "上传日志文件")
    @EventLog(EventType.UPLOAD_TAIL_FILE)
    public HttpWrapper<?> uploadFile(@RequestParam("files") List<MultipartFile> files) throws IOException {
        // 检查文件
        Valid.notEmpty(files);
        List<FileTailRequest> requestFiles = Lists.newList();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            if (fileName == null) {
                fileName = Utils.getRandomSuffix() + Const.DOT + Const.SUFFIX_LOG;
            }
            Path localAbsolutePath = Paths.get(SystemEnvAttr.TAIL_FILE_UPLOAD_PATH.getValue(), Utils.getRandomSuffix() + Const.DASHED + fileName);
            Files1.touch(localAbsolutePath);
            file.transferTo(localAbsolutePath);
            // 设置文件数据
            FileTailRequest request = new FileTailRequest();
            request.setName(fileName);
            request.setPath(Files1.getPath(localAbsolutePath.toString()));
            requestFiles.add(request);
        }
        // 保存
        fileTailService.uploadTailFiles(requestFiles);
        return HttpWrapper.ok();
    }

    @DemoDisableApi
    @PostMapping("/delete")
    @ApiOperation(value = "删除日志文件")
    @EventLog(EventType.DELETE_TAIL_FILE)
    public Integer deleteTailFile(@RequestBody FileTailRequest request) {
        List<Long> idList = Valid.notEmpty(request.getIdList());
        return fileTailService.deleteTailFile(idList);
    }

    @PostMapping("/list")
    @ApiOperation(value = "获取日志文件列表")
    public DataGrid<FileTailVO> tailFileList(@RequestBody FileTailRequest request) {
        return fileTailService.tailFileList(request);
    }

    @PostMapping("/detail")
    @ApiOperation(value = "获取日志文件详情")
    public FileTailVO tailFileDetail(@RequestBody FileTailRequest request) {
        Long id = Valid.notNull(request.getId());
        return fileTailService.tailFileDetail(id);
    }

    @PostMapping("/clean-ansi")
    @ApiOperation(value = "清除ANSI码")
    public void cleanAnsiCode(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {
        // 设置 http 响应头
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            fileName = Utils.getRandomSuffix() + Const.DOT + Const.SUFFIX_LOG;
        }
        Servlets.setAttachmentHeader(response, fileName);
        // 读取文件
        try (InputStream in = file.getInputStream()) {
            byte[] bytes = Streams.toByteArray(in);
            String clearValue = Utils.cleanStainAnsiCode(Strings.str(bytes));
            ServletOutputStream out = response.getOutputStream();
            out.write(Strings.bytes(clearValue));
            out.flush();
        }
    }

    @PostMapping("/config")
    @ApiOperation(value = "获取机器默认配置")
    public FileTailConfigVO getMachineConfig(@RequestBody FileTailRequest request) {
        Long machineId = Valid.notNull(request.getMachineId());
        return fileTailService.getMachineConfig(machineId);
    }

    @PostMapping("/write")
    @ApiOperation(value = "写入命令")
    public void write(@RequestBody FileTailRequest request) {
        String token = Valid.notBlank(request.getToken());
        String command = Valid.notEmpty(request.getCommand());
        fileTailService.writeCommand(token, command);
    }

}
