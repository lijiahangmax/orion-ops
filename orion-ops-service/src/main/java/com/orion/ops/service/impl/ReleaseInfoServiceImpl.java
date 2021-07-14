package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.Const;
import com.orion.ops.dao.*;
import com.orion.ops.entity.domain.*;
import com.orion.ops.entity.request.ApplicationReleaseBillRequest;
import com.orion.ops.entity.vo.ReleaseBillDetailVO;
import com.orion.ops.entity.vo.ReleaseBillListVO;
import com.orion.ops.entity.vo.ReleaseBillLogVO;
import com.orion.ops.service.api.ReleaseInfoService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.DataQuery;
import com.orion.utils.Strings;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 上线单信息查询service 实现
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/12 17:15
 */
@Service("releaseInfoService")
public class ReleaseInfoServiceImpl implements ReleaseInfoService {

    @Resource
    private ReleaseBillDAO releaseBillDAO;

    @Resource
    private ReleaseMachineDAO releaseMachineDAO;

    @Resource
    private ReleaseAppEnvDAO releaseAppEnvDAO;

    @Resource
    private ReleaseMachineEnvDAO releaseMachineEnvDAO;

    @Resource
    private ReleaseActionDAO releaseActionDAO;

    @Resource
    private ReleaseActionLogDAO releaseActionLogDAO;

    @Override
    public List<ReleaseMachineDO> getReleaseMachine(Long releaseId) {
        LambdaQueryWrapper<ReleaseMachineDO> wrapper = new LambdaQueryWrapper<ReleaseMachineDO>()
                .eq(ReleaseMachineDO::getReleaseId, releaseId);
        return releaseMachineDAO.selectList(wrapper);
    }

    @Override
    public List<ReleaseAppEnvDO> getReleaseAppEnv(Long releaseId) {
        LambdaQueryWrapper<ReleaseAppEnvDO> wrapper = new LambdaQueryWrapper<ReleaseAppEnvDO>()
                .eq(ReleaseAppEnvDO::getReleaseId, releaseId);
        return releaseAppEnvDAO.selectList(wrapper);
    }

    @Override
    public List<ReleaseMachineEnvDO> getReleaseMachineEnv(Long releaseId) {
        LambdaQueryWrapper<ReleaseMachineEnvDO> wrapper = new LambdaQueryWrapper<ReleaseMachineEnvDO>()
                .eq(ReleaseMachineEnvDO::getReleaseId, releaseId);
        return releaseMachineEnvDAO.selectList(wrapper);
    }

    @Override
    public List<ReleaseMachineEnvDO> getReleaseMachineEnv(Long releaseId, Long machineId) {
        LambdaQueryWrapper<ReleaseMachineEnvDO> wrapper = new LambdaQueryWrapper<ReleaseMachineEnvDO>()
                .eq(ReleaseMachineEnvDO::getReleaseId, releaseId)
                .eq(ReleaseMachineEnvDO::getMachineId, machineId);
        return releaseMachineEnvDAO.selectList(wrapper);
    }

    @Override
    public List<ReleaseActionDO> getReleaseAction(Long releaseId) {
        LambdaQueryWrapper<ReleaseActionDO> wrapper = new LambdaQueryWrapper<ReleaseActionDO>()
                .eq(ReleaseActionDO::getReleaseId, releaseId);
        return releaseActionDAO.selectList(wrapper);
    }

    @Override
    public ReleaseActionLogDO getReleaseActionLog(Long releaseActionId) {
        LambdaQueryWrapper<ReleaseActionLogDO> wrapper = new LambdaQueryWrapper<ReleaseActionLogDO>()
                .eq(ReleaseActionLogDO::getReleaseActionId, releaseActionId)
                .last(Const.LIMIT_1);
        return releaseActionLogDAO.selectOne(wrapper);
    }

    @Override
    public DataGrid<ReleaseBillListVO> releaseBillList(ApplicationReleaseBillRequest request) {
        Long userId = Currents.getUserId();
        LambdaQueryWrapper<ReleaseBillDO> wrapper = new LambdaQueryWrapper<ReleaseBillDO>()
                .like(!Strings.isBlank(request.getTitle()), ReleaseBillDO::getReleaseTitle, request.getTitle())
                .eq(Objects.nonNull(request.getAppId()), ReleaseBillDO::getAppId, request.getAppId())
                .eq(Objects.nonNull(request.getProfileId()), ReleaseBillDO::getProfileId, request.getProfileId())
                .eq(Objects.nonNull(request.getStatus()), ReleaseBillDO::getReleaseStatus, request.getStatus())
                .isNull(ReleaseBillDO::getRollbackReleaseId)
                .and(Const.ENABLE.equals(request.getOnlyMyself()), w -> w
                        .eq(ReleaseBillDO::getCreateUserId, userId)
                        .or()
                        .eq(ReleaseBillDO::getReleaseUserId, userId))
                .orderByDesc(ReleaseBillDO::getUpdateTime);
        return DataQuery.of(releaseBillDAO)
                .wrapper(wrapper)
                .page(request)
                .dataGrid(ReleaseBillListVO.class);
    }

    @Override
    public ReleaseBillDetailVO releaseBillDetail(Long id) {
        return null;
    }

    @Override
    public ReleaseBillLogVO releaseBillLog(Long id) {
        return null;
    }

}
