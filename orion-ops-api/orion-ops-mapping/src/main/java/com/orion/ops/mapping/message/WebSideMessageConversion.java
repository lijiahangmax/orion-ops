package com.orion.ops.mapping.message;

import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.entity.domain.WebSideMessageDO;
import com.orion.ops.entity.vo.message.WebSideMessageVO;

/**
 * 站内信 对象转换器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/8/10 18:24
 */
public class WebSideMessageConversion {

    static {
        TypeStore.STORE.register(WebSideMessageDO.class, WebSideMessageVO.class, p -> {
            WebSideMessageVO vo = new WebSideMessageVO();
            vo.setId(p.getId());
            vo.setClassify(p.getMessageClassify());
            vo.setType(p.getMessageType());
            vo.setStatus(p.getReadStatus());
            vo.setToUserId(p.getToUserId());
            vo.setToUserName(p.getToUserName());
            vo.setMessage(p.getSendMessage());
            vo.setParamsJson(p.getParamsJson());
            vo.setCreateTime(p.getCreateTime());
            vo.setCreateTimeAgo(Dates.ago(p.getCreateTime()));
            return vo;
        });
    }

}
