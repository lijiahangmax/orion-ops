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
package cn.orionsec.ops.service.api;

import cn.orionsec.ops.entity.request.sftp.FileUploadRequest;
import cn.orionsec.ops.entity.request.upload.BatchUploadRequest;
import cn.orionsec.ops.entity.vo.upload.BatchUploadCheckVO;
import cn.orionsec.ops.entity.vo.upload.BatchUploadTokenVO;

import java.util.List;

/**
 * 批量上传 service
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/13 15:48
 */
public interface BatchUploadService {

    /**
     * 批量上传检查机器以及文件
     *
     * @param request request
     * @return 检查信息
     */
    BatchUploadCheckVO checkMachineFiles(BatchUploadRequest request);

    /**
     * 获取上传 token
     *
     * @param request request
     * @return token
     */
    BatchUploadTokenVO getUploadAccessToken(BatchUploadRequest request);

    /**
     * 批量上传文件
     *
     * @param requestFiles requestFiles
     * @return fileToken
     */
    List<String> batchUpload(List<FileUploadRequest> requestFiles);

}
