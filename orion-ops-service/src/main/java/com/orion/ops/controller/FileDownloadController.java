package com.orion.ops.controller;

import com.orion.exception.NotFoundException;
import com.orion.ops.annotation.IgnoreAuth;
import com.orion.ops.annotation.IgnoreWrapper;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.download.FileDownloadType;
import com.orion.ops.entity.request.FileDownloadRequest;
import com.orion.ops.service.api.FileDownloadService;
import com.orion.ops.utils.Valid;
import com.orion.servlet.web.Servlets;
import com.orion.utils.Strings;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
     * 下载文件 检查文件存在以及权限
     */
    @RequestMapping("/token")
    public String getToken(@RequestBody FileDownloadRequest request) {
        Long id = Valid.notNull(request.getId());
        FileDownloadType type = Valid.notNull(FileDownloadType.of(request.getType()));
        return fileDownloadService.getDownloadToken(id, type);
    }

    /**
     * 下载文件
     */
    @RequestMapping("/{token}/exec")
    @IgnoreWrapper
    @IgnoreAuth
    public void downloadLogFile(@PathVariable String token, HttpServletResponse response) throws IOException {
        try {
            fileDownloadService.execDownload(token, response);
        } catch (NotFoundException e) {
            // 文件未找到
            Servlets.transfer(response, Const.EMPTY.getBytes(), Strings.bytes(Const.UNKNOWN, Const.UTF_8));
        }
    }

}
