package com.orion.ops.controller;

import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.IgnoreAuth;
import com.orion.ops.annotation.IgnoreWrapper;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.download.FileDownloadType;
import com.orion.ops.consts.tail.FileTailType;
import com.orion.ops.entity.dto.FileDownloadDTO;
import com.orion.ops.entity.request.FileDownloadRequest;
import com.orion.ops.entity.request.FileTailRequest;
import com.orion.ops.entity.vo.FileTailVO;
import com.orion.ops.service.api.FileService;
import com.orion.ops.utils.Valid;
import com.orion.servlet.web.Servlets;
import com.orion.utils.Charsets;
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
import java.util.Optional;

/**
 * 文件下载api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/8 17:17
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/file")
public class FileController {

    @Resource
    private FileService fileService;

    /**
     * 下载文件 检查文件存在以及权限
     */
    @RequestMapping("/download/token")
    @IgnoreWrapper
    public HttpWrapper<String> getToken(@RequestBody FileDownloadRequest request) {
        Long id = Valid.notNull(request.getId());
        FileDownloadType type = Valid.notNull(FileDownloadType.of(request.getType()));
        return fileService.getDownloadToken(id, type);
    }

    /**
     * 下载文件
     */
    @RequestMapping("/download/{token}")
    @IgnoreWrapper
    @IgnoreAuth
    public void downloadLogFile(@PathVariable String token, HttpServletResponse response) throws IOException {
        FileDownloadDTO downloadFile = fileService.getPathByDownloadToken(token);
        InputStream inputStream;
        String fileName;
        Optional<File> fileOptional = Optional.ofNullable(downloadFile)
                .map(FileDownloadDTO::getFilePath)
                .filter(Files1::isFile)
                .map(File::new);
        if (!fileOptional.isPresent()) {
            fileName = Const.UNKNOWN;
            inputStream = Streams.toInputStream(Const.UNKNOWN);
        } else {
            fileName = downloadFile.getFileName();
            inputStream = Files1.openInputStreamFastSafe(fileOptional.get());
        }
        try {
            Servlets.transfer(response, inputStream, fileName);
        } finally {
            Streams.close(inputStream);
        }
    }

    /**
     * tail文件 检查文件存在以及权限
     */
    @RequestMapping("/tail/token")
    @IgnoreWrapper
    public HttpWrapper<FileTailVO> getToken(@RequestBody FileTailRequest request) {
        Valid.notNull(FileTailType.of(request.getType()));
        Integer offset = request.getOffset();
        if (offset != null) {
            Valid.gte(offset, 0);
        }
        String charset = request.getCharset();
        if (charset != null) {
            Valid.isTrue(Charsets.isSupported(charset));
        }
        return fileService.getTailToken(request);
    }

}
