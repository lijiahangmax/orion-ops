package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.HistoryValueType;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.dao.CommandTemplateDAO;
import com.orion.ops.entity.domain.CommandTemplateDO;
import com.orion.ops.entity.dto.UserDTO;
import com.orion.ops.entity.request.CommandTemplateRequest;
import com.orion.ops.entity.vo.CommandTemplateVO;
import com.orion.ops.service.api.CommandTemplateService;
import com.orion.ops.service.api.HistoryValueService;
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

    @Resource
    private HistoryValueService historyValueService;

    @Override
    public Long addTemplate(CommandTemplateRequest request) {
        UserDTO user = Currents.getUser();
        CommandTemplateDO entity = new CommandTemplateDO();
        entity.setCreateUserId(user.getId());
        entity.setCreateUserName(user.getUsername());
        entity.setTemplateName(request.getName());
        entity.setTemplateValue(request.getValue());
        entity.setDescription(request.getDescription());
        commandTemplateDAO.insert(entity);
        return entity.getId();
    }

    @Override
    public Integer updateTemplate(CommandTemplateRequest request) {
        Long id = request.getId();
        CommandTemplateDO before = commandTemplateDAO.selectById(id);
        Valid.notNull(before, MessageConst.TEMPLATE_ABSENT);
        // 检查是否更新了值
        String value = request.getValue();
        String beforeValue = before.getTemplateValue();
        if (!Strings.isBlank(value) && !value.equals(beforeValue)) {
            historyValueService.addHistory(id, HistoryValueType.COMMAND_TEMPLATE, beforeValue);
        }
        // 更新
        CommandTemplateDO update = new CommandTemplateDO();
        update.setId(id);
        update.setTemplateName(request.getName());
        update.setTemplateValue(value);
        update.setDescription(request.getDescription());
        update.setUpdateTime(new Date());
        return commandTemplateDAO.updateById(update);
    }

    @Override
    public DataGrid<CommandTemplateVO> listTemplate(CommandTemplateRequest request) {
        LambdaQueryWrapper<CommandTemplateDO> wrapper = new LambdaQueryWrapper<CommandTemplateDO>()
                .eq(Objects.nonNull(request.getId()), CommandTemplateDO::getId, request.getId())
                .like(Strings.isNotBlank(request.getName()), CommandTemplateDO::getTemplateName, request.getName())
                .like(Strings.isNotBlank(request.getDescription()), CommandTemplateDO::getDescription, request.getDescription())
                .orderByDesc(CommandTemplateDO::getId);
        return DataQuery.of(commandTemplateDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(CommandTemplateVO.class);
    }

    @Override
    public CommandTemplateVO templateDetail(Long id) {
        CommandTemplateDO template = commandTemplateDAO.selectById(id);
        Valid.notNull(template, MessageConst.TEMPLATE_ABSENT);
        return Converts.to(template, CommandTemplateVO.class);
    }

    @Override
    public Integer deleteTemplate(Long id) {
        return commandTemplateDAO.deleteById(id);
    }

}
