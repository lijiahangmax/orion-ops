package com.orion.ops.controller;

import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.service.api.WebSideMessageService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 站内信 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/25 11:27
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/message")
public class WebSideMessageController {

    @Resource
    private WebSideMessageService webSideMessageService;

}
