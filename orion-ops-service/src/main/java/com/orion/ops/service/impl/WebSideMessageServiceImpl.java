package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.message.ReadStatus;
import com.orion.ops.dao.WebSideMessageDAO;
import com.orion.ops.entity.domain.WebSideMessageDO;
import com.orion.ops.entity.request.WebSideMessageRequest;
import com.orion.ops.entity.vo.WebSideMessageVO;
import com.orion.ops.service.api.WebSideMessageService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
import com.orion.utils.convert.Converts;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 站内信服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/3/25 11:26
 */
@Service("webSideMessageService")
public class WebSideMessageServiceImpl implements WebSideMessageService {

    @Resource
    private WebSideMessageDAO webSideMessageDAO;

    @Override
    public Integer getUnreadCount() {
        LambdaQueryWrapper<WebSideMessageDO> wrapper = new LambdaQueryWrapper<WebSideMessageDO>()
                .eq(WebSideMessageDO::getToUserId, Currents.getUserId());
        return webSideMessageDAO.selectCount(wrapper);
    }

    @Override
    public Integer setAllRead() {
        WebSideMessageDO update = new WebSideMessageDO();
        update.setReadStatus(ReadStatus.READ.getStatus());
        LambdaQueryWrapper<WebSideMessageDO> wrapper = new LambdaQueryWrapper<WebSideMessageDO>()
                .eq(WebSideMessageDO::getToUserId, Currents.getUserId())
                .eq(WebSideMessageDO::getReadStatus, ReadStatus.UNREAD.getStatus());
        return webSideMessageDAO.update(update, wrapper);
    }

    @Override
    public DataGrid<WebSideMessageVO> getMessageList(WebSideMessageRequest request) {
        LambdaQueryWrapper<WebSideMessageDO> wrapper = new LambdaQueryWrapper<WebSideMessageDO>()
                .eq(WebSideMessageDO::getToUserId, Currents.getUserId())
                .eq(Objects.nonNull(request.getClassify()), WebSideMessageDO::getMessageClassify, request.getClassify())
                .eq(Objects.nonNull(request.getType()), WebSideMessageDO::getMessageType, request.getType())
                .eq(Objects.nonNull(request.getStatus()), WebSideMessageDO::getReadStatus, request.getStatus());
        return DataQuery.of(webSideMessageDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(WebSideMessageVO.class);
    }

    @Override
    public WebSideMessageVO getMessageDetail(Long id) {
        WebSideMessageDO message = webSideMessageDAO.selectById(id);
        Valid.notNull(message, MessageConst.UNKNOWN_DATA);
        return Converts.to(message, WebSideMessageVO.class);
    }

    @Override
    public Integer deleteMessage(List<Long> idList) {
        return webSideMessageDAO.deleteBatchIds(idList);
    }

}
