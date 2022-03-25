package com.orion.ops.service.impl;

import com.orion.ops.dao.WebSideMessageDAO;
import com.orion.ops.service.api.WebSideMessageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 站内信服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/25 11:26
 */
@Service("webSideMessageService")
public class WebSideMessageServiceImpl implements WebSideMessageService {

    @Resource
    private WebSideMessageDAO webSideMessageDAO;

}
