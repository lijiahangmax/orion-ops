package com.orion.ops.handler.push;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.collect.Lists;
import com.orion.lang.utils.collect.Maps;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.io.InputStream;
import java.util.Map;

/**
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/7/18 16:04
 */
@Slf4j
public class DingPusher {


    public static void main(String[] args) {
        new DingPusher().messagePush("https://oapi.dingtalk.com/robot/send?access_token=");
    }


    @SneakyThrows
    public void messagePush(String webhook) {
        //文本内容组织
        StringBuilder text = new StringBuilder();
        InputStream s = DingPusher.class.getClassLoader().getResourceAsStream("templates/push/machine-alarm-ding.template");
        String s1 = IOUtils.toString(s);
        Map<Object, Object> params = Maps.newMap();
        params.put("machineName", "机器名称");
        params.put("machineHost", "机器主机");
        params.put("metrics", "CPU");
        params.put("usage", "84.5");
        params.put("time", "07:18 23:23:23");
        s1 = Strings.format(s1, params);
        text.append(s1);
        //钉钉actionCard推送对象
        OapiRobotSendRequest.Markdown actionCard = new OapiRobotSendRequest.Markdown();
        //推送透出首页标题 （你收到这条推送的时候会显示的初内容
        actionCard.setTitle("机器发生报警");
        //放入你组织好的内容
        actionCard.setText(text.toString());
        //做推送
        OapiRobotSendResponse resp = robotPushActionCardMessage(webhook, actionCard);
    }

    public OapiRobotSendResponse robotPushActionCardMessage(String webhook, OapiRobotSendRequest.Markdown actionCard) {
        try {
            DingTalkClient client = new DefaultDingTalkClient(webhook);
            OapiRobotSendRequest request = new OapiRobotSendRequest();
            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            at.setAtMobiles(Lists.of(""));
            at.setIsAtAll(false);
            // at.setIsAtAll(true);
            request.setAt(at);
            request.setMsgtype("markdown");
            request.setMarkdown(actionCard);
            OapiRobotSendResponse response = client.execute(request);
            if (!ObjectUtils.isEmpty(response) && response.isSuccess()) {
                log.info("钉钉机器人推送actionCard成功:{}", actionCard.getText());
            } else {
                log.error("钉钉机器人推送actionCard失败:{},{}", actionCard.getText(), response.getErrmsg());
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
