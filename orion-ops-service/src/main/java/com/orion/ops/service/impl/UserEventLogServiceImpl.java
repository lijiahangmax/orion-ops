package com.orion.ops.service.impl;

import com.orion.ops.dao.UserEventLogDAO;
import com.orion.ops.service.api.UserEventLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户日志实现
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/1/22 20:21
 */
@Service("userEventLogService")
public class UserEventLogServiceImpl implements UserEventLogService {

    @Resource
    private UserEventLogDAO userEventLogDAO;

}
