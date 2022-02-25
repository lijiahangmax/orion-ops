package com.orion.ops.entity.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * cron 下次执行时间
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/23 10:48
 */
@Data
public class CronNextVO {

    /**
     * 是否有效
     */
    private Boolean valid;

    /**
     * 下次执行时间
     */
    private List<Date> next;

}
