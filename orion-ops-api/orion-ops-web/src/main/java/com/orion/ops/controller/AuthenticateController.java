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
package com.orion.ops.controller;

import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.lang.utils.Objects1;
import com.orion.lang.utils.convert.Converts;
import com.orion.ops.annotation.DemoDisableApi;
import com.orion.ops.annotation.EventLog;
import com.orion.ops.annotation.IgnoreAuth;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.event.EventType;
import com.orion.ops.entity.request.user.UserLoginRequest;
import com.orion.ops.entity.request.user.UserResetRequest;
import com.orion.ops.entity.vo.user.UserInfoVO;
import com.orion.ops.entity.vo.user.UserLoginVO;
import com.orion.ops.service.api.PassportService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.Valid;
import com.orion.web.servlet.web.Servlets;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户认证 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/1 17:05
 */
@Api(tags = "用户认证")
@RestController
@RestWrapper
@RequestMapping("/orion/api/auth")
public class AuthenticateController {

    @Resource
    private PassportService passportService;

    @IgnoreAuth
    @PostMapping("/login")
    @ApiOperation(value = "登录")
    @EventLog(EventType.LOGIN)
    public UserLoginVO login(@RequestBody UserLoginRequest login, HttpServletRequest request) {
        String username = Valid.notBlank(login.getUsername()).trim();
        String password = Valid.notBlank(login.getPassword()).trim();
        login.setUsername(username);
        login.setPassword(password);
        login.setIp(Servlets.getRemoteAddr(request));
        // 登录
        return passportService.login(login);
    }

    @IgnoreAuth
    @GetMapping("/logout")
    @ApiOperation(value = "登出")
    @EventLog(EventType.LOGOUT)
    public HttpWrapper<?> logout() {
        passportService.logout();
        return HttpWrapper.ok();
    }

    @DemoDisableApi
    @PostMapping("/reset")
    @ApiOperation(value = "重置密码")
    @EventLog(EventType.RESET_PASSWORD)
    public HttpWrapper<?> resetPassword(@RequestBody UserResetRequest request) {
        String password = Valid.notBlank(request.getPassword()).trim();
        request.setUserId(Objects1.def(request.getUserId(), Currents::getUserId));
        request.setPassword(password);
        passportService.resetPassword(request);
        return HttpWrapper.ok();
    }

    @GetMapping("/valid")
    @ApiOperation(value = "检查用户信息")
    public UserInfoVO validToken() {
        return Converts.to(Currents.getUser(), UserInfoVO.class);
    }

}
