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
package cn.orionsec.ops.entity.vo.sftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * sftp 文件详情响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/23 18:36
 */
@Data
@ApiModel(value = "文件详情响应")
public class FileDetailVO {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "绝对路径")
    private String path;

    @ApiModelProperty(value = "文件大小")
    private String size;

    @ApiModelProperty(value = "文件大小(byte)")
    private Long sizeByte;

    @ApiModelProperty(value = "属性")
    private String attr;

    @ApiModelProperty(value = "10进制表现的8进制权限")
    private Integer permission;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "组id")
    private Integer gid;

    @ApiModelProperty(value = "更新时间")
    private Date modifyTime;

    @ApiModelProperty(value = "是否为目录")
    private Boolean isDir;

    @ApiModelProperty(value = "是否安全")
    private Boolean isSafe;

}
