/*
 * Copyright (c) 2021 - present Jiahang Li All rights reserved.
 *
 *   https://ops.orionsec.cn
 *
 * Members:
 *   Jiahang Li - ljh1553488six@139.com - author
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

import cn.orionsec.kit.lang.utils.convert.Converts;
import cn.orionsec.ops.dao.ApplicationInfoDAO;
import cn.orionsec.ops.dao.ApplicationPipelineDetailDAO;
import cn.orionsec.ops.entity.domain.ApplicationInfoDO;
import cn.orionsec.ops.entity.domain.ApplicationPipelineDetailDO;
import cn.orionsec.ops.entity.vo.app.ApplicationPipelineDetailVO;
import cn.orionsec.ops.service.api.ApplicationPipelineDetailService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用流水线详情服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/4/2 10:56
 */
@Service("applicationPipelineDetailService")
public class ApplicationPipelineDetailServiceImpl implements ApplicationPipelineDetailService {

    @Resource
    private ApplicationPipelineDetailDAO applicationPipelineDetailDAO;

    @Resource
    private ApplicationInfoDAO applicationInfoDAO;

    @Override
    public List<ApplicationPipelineDetailVO> getByPipelineId(Long pipelineId) {
        Wrapper<ApplicationPipelineDetailDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineDetailDO>()
                .eq(ApplicationPipelineDetailDO::getPipelineId, pipelineId);
        // 查询列表
        List<ApplicationPipelineDetailDO> pipelineDetails = applicationPipelineDetailDAO.selectList(wrapper);
        List<ApplicationPipelineDetailVO> details = Converts.toList(pipelineDetails, ApplicationPipelineDetailVO.class);
        // 查询应用名称
        List<Long> appIdList = details.stream()
                .map(ApplicationPipelineDetailVO::getAppId)
                .collect(Collectors.toList());
        List<ApplicationInfoDO> appList = applicationInfoDAO.selectBatchIds(appIdList);
        for (ApplicationPipelineDetailVO detail : details) {
            appList.stream()
                    .filter(s -> s.getId().equals(detail.getAppId()))
                    .findFirst()
                    .ifPresent(app -> {
                        detail.setAppName(app.getAppName());
                        detail.setAppTag(app.getAppTag());
                        detail.setRepoId(app.getRepoId());
                    });
        }
        return details;
    }

    @Override
    public List<ApplicationPipelineDetailDO> selectByPipelineId(Long pipelineId) {
        Wrapper<ApplicationPipelineDetailDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineDetailDO>()
                .eq(ApplicationPipelineDetailDO::getPipelineId, pipelineId);
        return applicationPipelineDetailDAO.selectList(wrapper);
    }

    @Override
    public List<ApplicationPipelineDetailDO> selectByPipelineIdList(List<Long> pipelineIdList) {
        Wrapper<ApplicationPipelineDetailDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineDetailDO>()
                .in(ApplicationPipelineDetailDO::getPipelineId, pipelineIdList);
        return applicationPipelineDetailDAO.selectList(wrapper);
    }

    @Override
    public Integer deleteByPipelineId(Long pipelineId) {
        Wrapper<ApplicationPipelineDetailDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineDetailDO>()
                .eq(ApplicationPipelineDetailDO::getPipelineId, pipelineId);
        return applicationPipelineDetailDAO.delete(wrapper);
    }

    @Override
    public Integer deleteByPipelineIdList(List<Long> pipelineIdList) {
        Wrapper<ApplicationPipelineDetailDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineDetailDO>()
                .in(ApplicationPipelineDetailDO::getPipelineId, pipelineIdList);
        return applicationPipelineDetailDAO.delete(wrapper);
    }

    @Override
    public Integer deleteByProfileId(Long profileId) {
        Wrapper<ApplicationPipelineDetailDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineDetailDO>()
                .eq(ApplicationPipelineDetailDO::getProfileId, profileId);
        return applicationPipelineDetailDAO.delete(wrapper);
    }

    @Override
    public Integer deleteByAppId(Long appId) {
        Wrapper<ApplicationPipelineDetailDO> wrapper = new LambdaQueryWrapper<ApplicationPipelineDetailDO>()
                .eq(ApplicationPipelineDetailDO::getAppId, appId);
        return applicationPipelineDetailDAO.delete(wrapper);
    }

}
