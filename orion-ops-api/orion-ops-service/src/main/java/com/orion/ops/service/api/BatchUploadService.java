package com.orion.ops.service.api;

import com.orion.ops.entity.request.upload.BatchUploadRequest;
import com.orion.ops.entity.request.sftp.FileUploadRequest;
import com.orion.ops.entity.vo.upload.BatchUploadCheckVO;
import com.orion.ops.entity.vo.upload.BatchUploadTokenVO;

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
