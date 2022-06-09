package com.orion.ops.entity.vo;

import com.orion.utils.convert.TypeStore;
import com.orion.utils.time.Dates;
import com.orion.vcs.git.info.LogInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Optional;

/**
 * 分支提交信息响应
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/12 17:57
 */
@Data
@ApiModel(value = "分支提交信息响应")
public class ApplicationVcsCommitVO {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "提交信息")
    private String message;

    @ApiModelProperty(value = "提交人")
    private String name;

    @ApiModelProperty(value = "提交时间")
    private Date time;

    @ApiModelProperty(value = "提交时间")
    private String timeAgo;

    static {
        TypeStore.STORE.register(LogInfo.class, ApplicationVcsCommitVO.class, p -> {
            ApplicationVcsCommitVO vo = new ApplicationVcsCommitVO();
            vo.setId(p.getId());
            vo.setMessage(p.getMessage());
            vo.setName(p.getName());
            Date time = p.getTime();
            vo.setTime(time);
            Optional.ofNullable(time).map(Dates::ago).ifPresent(vo::setTimeAgo);
            return vo;
        });
    }

}
