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
package cn.orionsec.ops.service.api;

import cn.orionsec.ops.constant.download.FileDownloadType;
import cn.orionsec.ops.entity.dto.file.FileDownloadDTO;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 文件下载service
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/8 17:21
 */
public interface FileDownloadService {

    /**
     * 下载文件 检查文件是否存在
     *
     * @param id   id
     * @param type type
     * @return token
     */
    String getDownloadToken(Long id, FileDownloadType type);

    /**
     * 通过token获取下载文件路径
     *
     * @param token token
     * @return path
     */
    FileDownloadDTO getPathByDownloadToken(String token);

    /**
     * 执行下载
     *
     * @param token    token
     * @param response response
     * @throws IOException IOException
     */
    void execDownload(String token, HttpServletResponse response) throws IOException;

}
