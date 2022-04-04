package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.dao.ApplicationInfoDAO;
import com.orion.ops.dao.ApplicationPipelineDetailDAO;
import com.orion.ops.entity.domain.ApplicationInfoDO;
import com.orion.ops.entity.domain.ApplicationPipelineDetailDO;
import com.orion.ops.entity.vo.ApplicationPipelineDetailVO;
import com.orion.ops.service.api.ApplicationPipelineDetailService;
import com.orion.utils.convert.Converts;
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
        List<ApplicationInfoDO> appList = applicationInfoDAO.selectNameByIdList(appIdList);
        for (ApplicationPipelineDetailVO detail : details) {
            appList.stream()
                    .filter(s -> s.getId().equals(detail.getAppId()))
                    .findFirst()
                    .map(ApplicationInfoDO::getAppName)
                    .ifPresent(detail::setAppName);
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
