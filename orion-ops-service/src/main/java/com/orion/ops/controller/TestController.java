package com.orion.ops.controller;

import com.orion.lang.wrapper.HttpWrapper;
import com.orion.ops.annotation.IgnoreWrapper;
import com.orion.ops.annotation.RestWrapper;
import com.orion.ops.consts.UserHolder;
import com.orion.utils.Exceptions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/2 10:07
 */
@RestController
@RestWrapper
@RequestMapping("/orion/api/test")
public class TestController {

    @RequestMapping("/req")
    public HttpWrapper<?> req() {
        System.out.println("req");
        // Exceptions.impossible();
        return HttpWrapper.ok(UserHolder.get());
    }

    @RequestMapping("/req1")
    @IgnoreWrapper
    public Object req1() {
        System.out.println("req");
        return 1;
    }

    @RequestMapping("/req2")
    public Object req2() {
        System.out.println("req");
        return "2";
    }

    @RequestMapping("/req3")
    public HttpWrapper<?> req3() {
        System.out.println("req");
        Exceptions.impossible();
        return HttpWrapper.ok(UserHolder.get());
    }

}
