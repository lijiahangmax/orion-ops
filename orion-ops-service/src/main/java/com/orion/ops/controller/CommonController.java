package com.orion.ops.controller;

import com.alibaba.fastjson.JSON;
import com.orion.lang.utils.collect.Lists;
import com.orion.lang.utils.io.StreamReaders;
import com.orion.ops.OrionApplication;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.constant.user.RoleType;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.service.api.CommonService;
import com.orion.ops.utils.Currents;
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
