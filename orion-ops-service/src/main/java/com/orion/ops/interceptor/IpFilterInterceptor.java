package com.orion.ops.interceptor;

import com.orion.constant.StandardContentType;
import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.ResultCode;
import com.orion.ops.utils.Utils;
import com.orion.servlet.web.Servlets;
import com.orion.utils.Strings;
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
 * 认证过滤器
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
