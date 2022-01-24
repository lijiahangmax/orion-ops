package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.event.EventKeys;
import com.orion.ops.consts.event.EventParamsHolder;
import com.orion.ops.dao.CommandTemplateDAO;
import com.orion.ops.entity.domain.CommandTemplateDO;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.entity.request.CommandTemplateRequest;
import com.orion.ops.entity.vo.CommandTemplateVO;
import com.orion.ops.service.api.CommandTemplateService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
import com.orion.utils.Strings;
import com.orion.utils.convert.Converts;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * 命令模板实现
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/9 17:06
 */
@Service("commandTemplateService")
public class CommandTemplateServiceImpl implements CommandTemplateService {

    @Resource
    private CommandTemplateDAO commandTemplateDAO;

    @Override
    public Long addTemplate(CommandTemplateRequest request) {
        // 插入
        UserDTO user = Currents.getUser();
        CommandTemplateDO entity = new CommandTemplateDO();
        entity.setTemplateName(request.getName());
        entity.setTemplateValue(request.getValue());
        entity.setCreateUserId(user.getId());
        entity.setCreateUserName(user.getUsername());
        entity.setUpdateUserId(user.getId());
        entity.setUpdateUserName(user.getUsername());
        entity.setDescription(request.getDescription());
        commandTemplateDAO.insert(entity);
        // 设置日志参数
        EventParamsHolder.addParams(entity);
        return entity.getId();
    }

    @Override
    public Integer updateTemplate(CommandTemplateRequest request) {
        // 查询模板信息
        Long id = request.getId();
        CommandTemplateDO beforeTemplate = commandTemplateDAO.selectById(id);
        Valid.notNull(beforeTemplate, MessageConst.TEMPLATE_ABSENT);
        // 更新
        UserDTO user = Currents.getUser();
        CommandTemplateDO update = new CommandTemplateDO();
        update.setId(id);
        update.setTemplateName(request.getName());
        update.setTemplateValue(request.getValue());
        update.setDescription(request.getDescription());
        update.setUpdateUserId(user.getId());
        update.setUpdateUserName(user.getUsername());
        update.setUpdateTime(new Date());
        int effect = commandTemplateDAO.updateById(update);
        // 设置日志参数
        EventParamsHolder.addParams(update);
        EventParamsHolder.addParam(EventKeys.NAME, beforeTemplate.getTemplateName());
        return effect;
    }

    @Override
    public DataGrid<CommandTemplateVO> listTemplate(CommandTemplateRequest request) {
        LambdaQueryWrapper<CommandTemplateDO> wrapper = new LambdaQueryWrapper<CommandTemplateDO>()
                .eq(Objects.nonNull(request.getId()), CommandTemplateDO::getId, request.getId())
                .like(Strings.isNotBlank(request.getName()), CommandTemplateDO::getTemplateName, request.getName())
                .like(Strings.isNotBlank(request.getValue()), CommandTemplateDO::getTemplateValue, request.getValue())
                .like(Strings.isNotBlank(request.getDescription()), CommandTemplateDO::getDescription, request.getDescription())
                .orderByDesc(CommandTemplateDO::getId);
        // 查询列表
        DataGrid<CommandTemplateVO> dataGrid = DataQuery.of(commandTemplateDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(CommandTemplateVO.class);
        // 省略模板
        if (request.isOmitValue()) {
            dataGrid.forEach(t -> t.setValue(Strings.omit(t.getValue(), Const.TEMPLATE_OMIT)));
        }
        return dataGrid;
    }

    @Override
    public CommandTemplateVO templateDetail(Long id) {
        CommandTemplateDO template = commandTemplateDAO.selectById(id);
        Valid.notNull(template, MessageConst.TEMPLATE_ABSENT);
        return Converts.to(template, CommandTemplateVO.class);
    }

    @Override
    public Integer deleteTemplate(Long id) {
        // 查询模板信息
        CommandTemplateDO beforeTemplate = commandTemplateDAO.selectById(id);
        Valid.notNull(beforeTemplate, MessageConst.TEMPLATE_ABSENT);
        // 删除
        int effect = commandTemplateDAO.deleteById(id);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.NAME, beforeTemplate.getTemplateName());
        return effect;
    }

}
