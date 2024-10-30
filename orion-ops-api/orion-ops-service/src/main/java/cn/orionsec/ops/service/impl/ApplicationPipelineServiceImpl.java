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
import cn.orionsec.ops.dao.ApplicationInfoDAO;
import cn.orionsec.ops.dao.ApplicationPipelineDAO;
import cn.orionsec.ops.dao.ApplicationPipelineDetailDAO;
import cn.orionsec.ops.entity.domain.ApplicationInfoDO;
import cn.orionsec.ops.entity.domain.ApplicationPipelineDO;
import cn.orionsec.ops.entity.domain.ApplicationPipelineDetailDO;
import cn.orionsec.ops.entity.request.app.ApplicationPipelineRequest;
import cn.orionsec.ops.entity.vo.app.ApplicationPipelineDetailVO;
import cn.orionsec.ops.entity.vo.app.ApplicationPipelineVO;
import cn.orionsec.ops.service.api.ApplicationPipelineDetailService;
import cn.orionsec.ops.service.api.ApplicationPipelineService;
import cn.orionsec.ops.utils.DataQuery;
import cn.orionsec.ops.utils.EventParamsHolder;
import cn.orionsec.ops.utils.Valid;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.convert.Converts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 应用流水线服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/2 10:16
 */
@Service("applicationPipelineService")
public class ApplicationPipelineServiceImpl implements ApplicationPipelineService {

    @Resource
    private ApplicationPipelineDAO applicationPipelineDAO;

    @Resource
    private ApplicationPipelineDetailDAO applicationPipelineDetailDAO;

    @Resource
    private ApplicationPipelineDetailService applicationPipelineDetailService;

    @Resource
    private ApplicationInfoDAO applicationInfoDAO;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addPipeline(ApplicationPipelineRequest request) {
        Long profileId = request.getProfileId();
        String name = request.getName();
        // 检查名称是否重复
        this.checkNamePresent(null, profileId, name);
        // 插入主表
        ApplicationPipelineDO insertPipeline = new ApplicationPipelineDO();
        insertPipeline.setProfileId(profileId);
        insertPipeline.setPipelineName(name);
        insertPipeline.setDescription(request.getDescription());
        applicationPipelineDAO.insert(insertPipeline);
        Long id = insertPipeline.getId();
        // 插入详情
        List<ApplicationPipelineDetailDO> details = request.getDetails().stream()
                .map(s -> {
                    ApplicationPipelineDetailDO pipelineDetail = new ApplicationPipelineDetailDO();
                    pipelineDetail.setPipelineId(id);
                    pipelineDetail.setAppId(s.getAppId());
                    pipelineDetail.setProfileId(profileId);
                    pipelineDetail.setStageType(s.getStageType());
                    return pipelineDetail;
                }).collect(Collectors.toList());
        details.forEach(applicationPipelineDetailDAO::insert);
        // 设置日志参数
        EventParamsHolder.addParams(insertPipeline);
        EventParamsHolder.addParam(EventKeys.DETAILS, details);
        return id;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updatePipeline(ApplicationPipelineRequest request) {
        Long id = request.getId();
        Long profileId = request.getProfileId();
        String name = request.getName();
        // 检查名称是否重复
        this.checkNamePresent(id, profileId, name);
        // 更新主表
        ApplicationPipelineDO updatePipeline = new ApplicationPipelineDO();
        updatePipeline.setId(id);
        updatePipeline.setProfileId(profileId);
        updatePipeline.setPipelineName(name);
        updatePipeline.setDescription(request.getDescription());
        int effect = applicationPipelineDAO.updateById(updatePipeline);
        // 删除详情
        applicationPipelineDetailService.deleteByPipelineId(id);
        // 重新插入详情
        List<ApplicationPipelineDetailDO> details = request.getDetails().stream()
                .map(s -> {
                    ApplicationPipelineDetailDO pipelineDetail = new ApplicationPipelineDetailDO();
                    pipelineDetail.setPipelineId(id);
                    pipelineDetail.setStageType(s.getStageType());
                    pipelineDetail.setAppId(s.getAppId());
                    pipelineDetail.setProfileId(profileId);
                    return pipelineDetail;
                }).collect(Collectors.toList());
        details.forEach(applicationPipelineDetailDAO::insert);
        // 设置日志参数
        EventParamsHolder.addParams(updatePipeline);
        EventParamsHolder.addParam(EventKeys.DETAILS, details);
        return effect;
    }

    @Override
    public DataGrid<ApplicationPipelineVO> listPipeline(ApplicationPipelineRequest request) {
        LambdaQueryWrapper<ApplicationPipelineDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineDO>()
                .eq(Objects.nonNull(request.getId()), ApplicationPipelineDO::getId, request.getId())
                .eq(Objects.nonNull(request.getProfileId()), ApplicationPipelineDO::getProfileId, request.getProfileId())
                .like(Strings.isNotBlank(request.getName()), ApplicationPipelineDO::getPipelineName, request.getName())
                .like(Strings.isNotBlank(request.getDescription()), ApplicationPipelineDO::getDescription, request.getDescription());
        // 查询列表
        DataGrid<ApplicationPipelineVO> dataGrid = DataQuery.of(applicationPipelineDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(ApplicationPipelineVO.class);
        // 不查询详情直接返回
        if (!Const.ENABLE.equals(request.getQueryDetail())) {
            return dataGrid;
        }
        // 获取流水线详情
        List<Long> pipelineIdList = dataGrid.stream()
                .map(ApplicationPipelineVO::getId)
                .collect(Collectors.toList());
        if (pipelineIdList.isEmpty()) {
            return dataGrid;
        }
        List<ApplicationPipelineDetailDO> pipelineDetails = applicationPipelineDetailService.selectByPipelineIdList(pipelineIdList);
        List<ApplicationPipelineDetailVO> details = Converts.toList(pipelineDetails, ApplicationPipelineDetailVO.class);
        // 获取应用名称
        if (!details.isEmpty()) {
            List<Long> appIdList = details.stream()
                    .map(ApplicationPipelineDetailVO::getAppId)
                    .distinct()
                    .collect(Collectors.toList());
            List<ApplicationInfoDO> appNameList = applicationInfoDAO.selectBatchIds(appIdList);
            // 设置应用名称
            for (ApplicationPipelineDetailVO detail : details) {
                appNameList.stream()
                        .filter(s -> s.getId().equals(detail.getAppId()))
                        .findFirst()
                        .ifPresent(app -> {
                            detail.setRepoId(app.getRepoId());
                            detail.setAppName(app.getAppName());
                            detail.setAppTag(app.getAppTag());
                        });
            }
        }
        // 设置流水线详情
        dataGrid.forEach(s -> {
            List<ApplicationPipelineDetailVO> currentPipelineDetails = details.stream()
                    .filter(d -> d.getPipelineId().equals(s.getId()))
                    .collect(Collectors.toList());
            s.setDetails(currentPipelineDetails);
        });
        return dataGrid;
    }

    @Override
    public ApplicationPipelineVO getPipeline(Long id) {
        // 查询主表
        ApplicationPipelineDO pipeline = applicationPipelineDAO.selectById(id);
        Valid.notNull(pipeline, MessageConst.UNKNOWN_DATA);
        ApplicationPipelineVO pipelineVO = Converts.to(pipeline, ApplicationPipelineVO.class);
        // 查询子表
        List<ApplicationPipelineDetailVO> details = applicationPipelineDetailService.getByPipelineId(id);
        pipelineVO.setDetails(details);
        return pipelineVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deletePipeline(List<Long> idList) {
        // 删除主表
        int effect = applicationPipelineDAO.deleteBatchIds(idList);
        // 删除详情表
        effect += applicationPipelineDetailService.deleteByPipelineIdList(idList);
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID_LIST, idList);
        EventParamsHolder.addParam(EventKeys.COUNT, idList.size());
        return effect;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteByProfileId(Long profileId) {
        // 删除主表
        LambdaQueryWrapper<ApplicationPipelineDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineDO>()
                .eq(ApplicationPipelineDO::getProfileId, profileId);
        int effect = applicationPipelineDAO.delete(wrapper);
        // 删除详情
        effect += applicationPipelineDetailService.deleteByProfileId(profileId);
        return effect;
    }

    /**
     * 检查名称是否存在
     *
     * @param id        id
     * @param profileId profileId
     * @param name      name
     */
    private void checkNamePresent(Long id, Long profileId, String name) {
        LambdaQueryWrapper<ApplicationPipelineDO> presentWrapper = new LambdaQueryWrapper<ApplicationPipelineDO>()
                .ne(id != null, ApplicationPipelineDO::getId, id)
                .eq(ApplicationPipelineDO::getProfileId, profileId)
                .eq(ApplicationPipelineDO::getPipelineName, name);
        boolean present = DataQuery.of(applicationPipelineDAO).wrapper(presentWrapper).present();
        Valid.isTrue(!present, MessageConst.NAME_PRESENT);
    }

}
