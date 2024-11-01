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
package cn.orionsec.ops.controller;

import cn.orionsec.kit.lang.define.wrapper.HttpWrapper;
import cn.orionsec.kit.lang.utils.Objects1;
import cn.orionsec.kit.lang.utils.convert.Converts;
import cn.orionsec.kit.web.servlet.web.Servlets;
import cn.orionsec.ops.annotation.DemoDisableApi;
import cn.orionsec.ops.annotation.EventLog;
import cn.orionsec.ops.annotation.IgnoreAuth;
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.constant.event.EventType;
import cn.orionsec.ops.entity.request.user.UserLoginRequest;
import cn.orionsec.ops.entity.request.user.UserResetRequest;
import cn.orionsec.ops.entity.vo.user.UserInfoVO;
import cn.orionsec.ops.entity.vo.user.UserLoginVO;
import cn.orionsec.ops.service.api.PassportService;
import cn.orionsec.ops.utils.Currents;
import cn.orionsec.ops.utils.Valid;
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
