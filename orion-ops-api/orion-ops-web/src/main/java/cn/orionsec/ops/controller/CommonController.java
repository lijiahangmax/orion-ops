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

import cn.orionsec.ops.OrionApplication;
import cn.orionsec.ops.annotation.RestWrapper;
import cn.orionsec.ops.constant.user.RoleType;
import cn.orionsec.ops.entity.dto.user.UserDTO;
import cn.orionsec.ops.service.api.CommonService;
import cn.orionsec.ops.utils.Currents;
import com.alibaba.fastjson.JSON;
import com.orion.lang.utils.collect.Lists;
import com.orion.lang.utils.io.StreamReaders;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 公共 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/11/10 17:37
 */
@Api(tags = "公共接口")
@RestController
@RestWrapper
@RequestMapping("/orion/api/common")
public class CommonController {

    @Resource
    private CommonService commonService;

    @GetMapping("/menu")
    @ApiOperation(value = "获取菜单")
    public List<?> getMenu() throws IOException {
        UserDTO user = Currents.getUser();
        String menuFile = RoleType.of(user.getRoleType()).getMenuPath();
        InputStream menu = OrionApplication.class.getResourceAsStream(menuFile);
        if (menu == null) {
            return Lists.empty();
        }
        return JSON.parseArray(new String(StreamReaders.readAllBytes(menu)));
    }

}
