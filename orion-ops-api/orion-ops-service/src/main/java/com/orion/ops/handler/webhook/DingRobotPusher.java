package com.orion.ops.handler.webhook;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 钉钉机器人推送器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/29 18:19
 */
@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DingRobotPusher implements IWebhookPusher {

    /**
     * 推送 url
     */
    private String url;

    /**
     * 推送标题
     */
    private String title;

    /**
     * 推送内容
     */
    private String text;

    /**
     * @ 用户的手机号
     */
    private List<String> atMobiles;

    @Override
    public void push() {
        OapiRobotSendRequest.Markdown content = new OapiRobotSendRequest.Markdown();
        content.setTitle(title);
        content.setText(text);
        // 执行推送请求
        DingTalkClient client = new DefaultDingTalkClient(url);
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("markdown");
        request.setMarkdown(content);
        if (!Lists.isEmpty(atMobiles)) {
            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            at.setAtMobiles(atMobiles);
            request.setAt(at);
        }
        try {
            OapiRobotSendResponse response = client.execute(request);
            if (!response.isSuccess()) {
                log.error("钉钉机器人推送失败 url: {}", url);
            }
        } catch (Exception e) {
            log.error("钉钉机器人推送异常 url: {}", url, e);
            throw Exceptions.httpRequest(url, "ding push error", e);
        }
    }

}
