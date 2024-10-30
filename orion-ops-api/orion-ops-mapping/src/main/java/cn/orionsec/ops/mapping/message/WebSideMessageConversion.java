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
package cn.orionsec.ops.mapping.message;

import cn.orionsec.ops.entity.domain.WebSideMessageDO;
import cn.orionsec.ops.entity.vo.message.WebSideMessageVO;
import com.orion.lang.utils.convert.TypeStore;
import com.orion.lang.utils.time.Dates;

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
            vo.setRelId(p.getRelId());
            vo.setCreateTime(p.getCreateTime());
            vo.setCreateTimeAgo(Dates.ago(p.getCreateTime()));
            return vo;
        });
    }

}
