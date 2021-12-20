package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.dao.ApplicationBuildDAO;
import com.orion.ops.dao.ApplicationReleaseActionDAO;
import com.orion.ops.dao.ApplicationReleaseDAO;
import com.orion.ops.dao.ApplicationReleaseMachineDAO;
import com.orion.ops.entity.domain.ApplicationReleaseDO;
import com.orion.ops.entity.domain.ApplicationReleaseMachineDO;
import com.orion.ops.entity.request.ApplicationReleaseAuditRequest;
import com.orion.ops.entity.request.ApplicationReleaseRequest;
import com.orion.ops.entity.vo.*;
import com.orion.ops.service.api.ApplicationReleaseActionService;
import com.orion.ops.service.api.ApplicationReleaseMachineService;
import com.orion.ops.service.api.ApplicationReleaseService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
import com.orion.utils.Strings;
import com.orion.utils.convert.Converts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 发布单 服务实现类
 * </p>
 *
 * @author Jiahang Li
 * @since 2021-12-20
 */
@Service("applicationReleaseService")
public class ApplicationReleaseServiceImpl implements ApplicationReleaseService {

    @Resource
    private ApplicationReleaseDAO applicationReleaseDAO;

    @Resource
    private ApplicationReleaseMachineService applicationReleaseMachineService;

    @Resource
    private ApplicationReleaseMachineDAO applicationReleaseMachineDAO;

    @Resource
    private ApplicationReleaseActionService applicationReleaseActionService;

    @Resource
    private ApplicationReleaseActionDAO applicationReleaseActionDAO;

    @Resource
    private ApplicationBuildDAO applicationBuildDAO;

    @Override
    public DataGrid<ApplicationReleaseListVO> getReleaseList(ApplicationReleaseRequest request) {
        Long userId = Currents.getUserId();
        LambdaQueryWrapper<ApplicationReleaseDO> wrapper = new LambdaQueryWrapper<ApplicationReleaseDO>()
                .like(!Strings.isBlank(request.getTitle()), ApplicationReleaseDO::getReleaseTitle, request.getTitle())
                .like(!Strings.isBlank(request.getDescription()), ApplicationReleaseDO::getReleaseDescription, request.getDescription())
                .eq(Objects.nonNull(request.getAppId()), ApplicationReleaseDO::getAppId, request.getAppId())
                .eq(Objects.nonNull(request.getProfileId()), ApplicationReleaseDO::getProfileId, request.getProfileId())
                .eq(Objects.nonNull(request.getStatus()), ApplicationReleaseDO::getReleaseStatus, request.getStatus())
                .and(Const.ENABLE.equals(request.getOnlyMyself()), w -> w
                        .eq(ApplicationReleaseDO::getCreateUserId, userId)
                        .or()
                        .eq(ApplicationReleaseDO::getReleaseUserId, userId))
                .orderByDesc(ApplicationReleaseDO::getId);
        // 查询列表
        DataGrid<ApplicationReleaseListVO> dataGrid = DataQuery.of(applicationReleaseDAO)
                .wrapper(wrapper)
                .page(request)
                .dataGrid(ApplicationReleaseListVO.class);
        // 查询构建序列
        dataGrid.forEach(s -> Optional.ofNullable(s.getBuildId())
                .map(applicationBuildDAO::selectBuildSeq)
                .ifPresent(s::setBuildSeq));
        // 查询发布机器
        List<Long> machineIdList = dataGrid.stream().map(ApplicationReleaseListVO::getId).collect(Collectors.toList());
        if (!machineIdList.isEmpty()) {
            // 查询机器
            Map<Long, List<ApplicationReleaseMachineVO>> releaseMachineMap = applicationReleaseMachineService.getReleaseMachines(machineIdList)
                    .stream()
                    .map(s -> Converts.to(s, ApplicationReleaseMachineVO.class))
                    .collect(Collectors.groupingBy(ApplicationReleaseMachineVO::getReleaseId));
            dataGrid.forEach(release -> release.setMachines(releaseMachineMap.get(release.getId())));
        }
        return dataGrid;
    }

    @Override
    public ApplicationReleaseDetailVO getReleaseDetail(Long id) {
        // 查询
        ApplicationReleaseDO release = applicationReleaseDAO.selectById(id);
        Valid.notNull(release, MessageConst.UNKNOWN_DATA);
        ApplicationReleaseDetailVO vo = Converts.to(release, ApplicationReleaseDetailVO.class);
        // 查询构建序列
        Optional.ofNullable(vo.getBuildId())
                .map(applicationBuildDAO::selectBuildSeq)
                .ifPresent(vo::setBuildSeq);
        // 查询机器
        List<ApplicationReleaseMachineVO> machines = applicationReleaseMachineService.getReleaseMachines(id).stream()
                .map(s -> Converts.to(s, ApplicationReleaseMachineVO.class))
                .collect(Collectors.toList());
        vo.setMachines(machines);
        return vo;
    }

    @Override
    public ApplicationReleaseMachineVO getReleaseMachineDetail(Long releaseMachineId) {
        // 查询数据
        ApplicationReleaseMachineDO machine = applicationReleaseMachineDAO.selectById(releaseMachineId);
        Valid.notNull(machine, MessageConst.UNKNOWN_DATA);
        ApplicationReleaseMachineVO vo = Converts.to(machine, ApplicationReleaseMachineVO.class);
        // 查询action
        List<ApplicationReleaseActionVO> actions = applicationReleaseActionService.getReleaseActionByReleaseMachineId(releaseMachineId).stream()
                .map(s -> Converts.to(s, ApplicationReleaseActionVO.class))
                .collect(Collectors.toList());
        vo.setActions(actions);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitAppRelease(ApplicationReleaseRequest request) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long copyAppRelease(Long id) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer auditAppRelease(ApplicationReleaseAuditRequest request) {
        return null;
    }

    @Override
    public void runnableAppRelease(ApplicationReleaseRequest request) {

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long rollbackAppRelease(Long id) {
        return null;
    }

    @Override
    public List<ApplicationReleaseStatusVO> getReleaseStatusList(List<Long> idList) {
        return null;
    }

    @Override
    public ApplicationReleaseStatusVO getReleaseStatus(Long id) {
        return null;
    }

}
