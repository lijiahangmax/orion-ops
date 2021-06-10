package com.orion.ops.controller;

import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.IgnoreWrapper;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.DownloadType;
import com.orion.ops.entity.request.FileDownloadRequest;
import com.orion.ops.service.api.FileDownloadService;
import com.orion.ops.utils.Valid;
import com.orion.servlet.web.Servlets;
import com.orion.utils.io.Files1;
import com.orion.utils.io.Streams;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件下载api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/8 17:17
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/file-download")
public class FileDownloadController {

    @Resource
    private FileDownloadService fileDownloadService;

    /**
     * 检查文件存在以及权限
     */
    @RequestMapping("/check")
    @IgnoreWrapper
    public HttpWrapper<String> check(@RequestBody FileDownloadRequest request) {
        Long id = Valid.notNull(request.getId());
        DownloadType type = Valid.notNull(DownloadType.of(request.getType()));
        return fileDownloadService.checkFile(id, type);
    }

    /**
     * 下载文件
     */
    @RequestMapping("/{token}")
    @IgnoreWrapper
    public void downloadLogFile(@PathVariable String token, HttpServletResponse response) throws IOException {
        String filePath = fileDownloadService.getPathByToken(token);
        InputStream inputStream;
        String fileName;
        if (filePath == null || !Files1.isFile(filePath)) {
            fileName = Const.UNKNOWN;
            inputStream = Streams.toInputStream(Const.UNKNOWN);
        } else {
            File file = new File(filePath);
            fileName = Files1.getFileName(file);
            inputStream = Files1.openInputStreamFastSafe(file);
        }
        Servlets.transfer(response, inputStream, fileName);
    }

}
