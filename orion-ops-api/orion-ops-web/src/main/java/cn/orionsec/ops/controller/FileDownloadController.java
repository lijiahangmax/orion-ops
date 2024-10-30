/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
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

import cn.orionsec.ops.annotation.IgnoreAuth;
import cn.orionsec.ops.annotation.IgnoreWrapper;
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.download.FileDownloadType;
import cn.orionsec.ops.entity.request.file.FileDownloadRequest;
import cn.orionsec.ops.service.api.FileDownloadService;
import cn.orionsec.ops.utils.Valid;
import com.orion.lang.exception.NotFoundException;
import com.orion.web.servlet.web.Servlets;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 文件下载 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/8 17:17
 */
@Api(tags = "文件下载")
@RestController
@RestWrapper
@RequestMapping("/orion/api/file-download")
public class FileDownloadController {

    @Resource
    private FileDownloadService fileDownloadService;

    @PostMapping("/token")
    @ApiOperation(value = "检查并获取下载文件token")
    public String getDownloadToken(@RequestBody FileDownloadRequest request) {
        Long id = Valid.notNull(request.getId());
        FileDownloadType type = Valid.notNull(FileDownloadType.of(request.getType()));
        return fileDownloadService.getDownloadToken(id, type);
    }

    @IgnoreWrapper
    @IgnoreAuth
    @GetMapping("/{token}/exec")
    @ApiOperation(value = "下载文件")
    public void downloadLogFile(@PathVariable String token, HttpServletResponse response) throws IOException {
        try {
            fileDownloadService.execDownload(token, response);
        } catch (NotFoundException e) {
            // 文件未找到
            Servlets.transfer(response, Const.EMPTY.getBytes(), Const.UNKNOWN);
        }
    }

}
