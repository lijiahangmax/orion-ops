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
package cn.orionsec.ops.service.impl;

import cn.orionsec.ops.constant.Const;
import cn.orionsec.ops.constant.MessageConst;
import cn.orionsec.ops.constant.event.EventKeys;
import cn.orionsec.ops.dao.CommandTemplateDAO;
import cn.orionsec.ops.entity.domain.CommandTemplateDO;
import cn.orionsec.ops.entity.dto.user.UserDTO;
import cn.orionsec.ops.entity.request.template.CommandTemplateRequest;
import cn.orionsec.ops.entity.vo.template.CommandTemplateVO;
import cn.orionsec.ops.service.api.CommandTemplateService;
import cn.orionsec.ops.utils.Currents;
import cn.orionsec.ops.utils.DataQuery;
import cn.orionsec.ops.utils.EventParamsHolder;
import cn.orionsec.ops.utils.Valid;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.convert.Converts;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
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
        // 名称重复校验
        String name = request.getName();
        this.checkNamePresent(null, name);
        // 插入
        UserDTO user = Currents.getUser();
        CommandTemplateDO entity = new CommandTemplateDO();
        entity.setTemplateName(name);
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
        String name = request.getName();
        // 名称重复校验
        this.checkNamePresent(id, name);
        CommandTemplateDO beforeTemplate = commandTemplateDAO.selectById(id);
        Valid.notNull(beforeTemplate, MessageConst.TEMPLATE_ABSENT);
        // 更新
        UserDTO user = Currents.getUser();
        CommandTemplateDO update = new CommandTemplateDO();
        update.setId(id);
        update.setTemplateName(name);
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
    public Integer deleteTemplate(List<Long> idList) {
        // 删除
        int effect = commandTemplateDAO.deleteBatchIds(idList);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID_LIST, idList);
        EventParamsHolder.addParam(EventKeys.COUNT, idList.size());
        return effect;
    }

    /**
     * 检查名称是否存在
     *
     * @param id   id
     * @param name name
     */
    private void checkNamePresent(Long id, String name) {
        LambdaQueryWrapper<CommandTemplateDO> presentWrapper = new LambdaQueryWrapper<CommandTemplateDO>()
                .ne(id != null, CommandTemplateDO::getId, id)
                .eq(CommandTemplateDO::getTemplateName, name);
        boolean present = DataQuery.of(commandTemplateDAO).wrapper(presentWrapper).present();
        Valid.isTrue(!present, MessageConst.NAME_PRESENT);
    }

}
