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
package cn.orionsec.ops.interceptor;

import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.ResultCode;
import cn.orionsec.ops.utils.Utils;
import com.orion.lang.constant.StandardContentType;
import com.orion.lang.define.wrapper.HttpWrapper;
import com.orion.lang.utils.Strings;
import com.orion.web.servlet.web.Servlets;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * IP过滤拦截器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/1 17:20
 */
@Component
public class IpFilterInterceptor implements HandlerInterceptor {

    /**
     * 是否启用
     */
    private boolean enable;

    /**
     * 是否为白名单
     */
    private boolean isWhiteList;

    /**
     * 过滤器
     */
    private List<String> filters;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // 停用
        if (!enable) {
            return true;
        }
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        String ip = Servlets.getRemoteAddr(request);
        // 本机不过滤
        if (Const.LOCALHOST_IP_V4.equals(ip)) {
            return true;
        }
        // 过滤
        boolean contains = false;
        for (String filter : filters) {
            if (Strings.isBlank(filter)) {
                continue;
            }
            // 检测
            contains = Utils.checkIpIn(ip, filter);
            if (contains) {
                break;
            }
        }
        // 结果
        boolean pass;
        if (isWhiteList) {
            pass = contains;
        } else {
            pass = !contains;
        }
        // 返回
        if (!pass) {
            response.setContentType(StandardContentType.APPLICATION_JSON);
            Servlets.transfer(response, HttpWrapper.of(ResultCode.IP_BAN).toJsonString().getBytes());
        }
        return pass;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }

    /**
     * 配置启用类型
     *
     * @param enable      是否启用
     * @param isWhiteList 是否为白名单
     * @param filter      规则
     */
    public void set(boolean enable, boolean isWhiteList, String filter) {
        this.enable = enable;
        this.isWhiteList = isWhiteList;
        if (Strings.isBlank(filter)) {
            this.enable = false;
        } else {
            this.filters = Arrays.stream(filter.split(Const.LF))
                    .filter(Strings::isNotBlank)
                    .collect(Collectors.toList());
            if (filters.isEmpty()) {
                this.enable = false;
            }
        }
    }

}
