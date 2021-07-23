package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.dao.ReleaseActionDAO;
import com.orion.ops.dao.ReleaseBillDAO;
import com.orion.ops.dao.ReleaseMachineDAO;
import com.orion.ops.entity.domain.ReleaseActionDO;
import com.orion.ops.entity.domain.ReleaseBillDO;
import com.orion.ops.entity.domain.ReleaseMachineDO;
import com.orion.ops.entity.request.ApplicationReleaseBillRequest;
import com.orion.ops.entity.vo.*;
import com.orion.ops.service.api.ReleaseInfoService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
import com.orion.utils.Strings;
import com.orion.utils.convert.Converts;
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
    private ReleaseActionDAO releaseActionDAO;

    @Override
    public ReleaseBillDO getReleaseBill(Long id) {
        return releaseBillDAO.selectById(id);
    }

    @Override
    public List<ReleaseMachineDO> getReleaseMachine(Long releaseId) {
        LambdaQueryWrapper<ReleaseMachineDO> wrapper = new LambdaQueryWrapper<ReleaseMachineDO>()
                .eq(ReleaseMachineDO::getReleaseId, releaseId);
        return releaseMachineDAO.selectList(wrapper);
    }

    @Override
    public List<ReleaseActionDO> getReleaseAction(Long releaseId) {
        LambdaQueryWrapper<ReleaseActionDO> wrapper = new LambdaQueryWrapper<ReleaseActionDO>()
                .eq(ReleaseActionDO::getReleaseId, releaseId);
        return releaseActionDAO.selectList(wrapper);
    }

    @Override
    public List<ReleaseActionDO> getReleaseAction(Long releaseId, Long machineId) {
        LambdaQueryWrapper<ReleaseActionDO> wrapper = new LambdaQueryWrapper<ReleaseActionDO>()
                .eq(ReleaseActionDO::getReleaseId, releaseId)
                .eq(ReleaseActionDO::getMachineId, machineId);
        return releaseActionDAO.selectList(wrapper);
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
        // 查询上线单信息
        ReleaseBillDO releaseBill = Valid.notNull(releaseBillDAO.selectById(id), MessageConst.RELEASE_BILL_ABSENT);
        ReleaseBillDetailVO detailVO = Converts.to(releaseBill, ReleaseBillDetailVO.class);
        // 查询主机操作信息
        List<ReleaseActionDO> hostActions = this.getReleaseAction(id, Const.HOST_MACHINE_ID);
        List<ReleaseActionVO> hostActionVO = Converts.toList(hostActions, ReleaseActionVO.class);
        for (int i = 0; i < hostActionVO.size(); i++) {
            hostActionVO.get(i).setStep(i + 1);
        }
        detailVO.setHostActions(hostActionVO);
        // 查询机器信息
        List<ReleaseMachineDO> machines = this.getReleaseMachine(id);
        List<ReleaseMachineVO> machineVO = Converts.toList(machines, ReleaseMachineVO.class);
        detailVO.setMachines(machineVO);
        // 查询机器操作信息
        for (ReleaseMachineVO releaseMachineVO : machineVO) {
            List<ReleaseActionDO> targetActions = this.getReleaseAction(id, releaseMachineVO.getMachineId());
            List<ReleaseActionVO> targetActionVO = Converts.toList(targetActions, ReleaseActionVO.class);
            for (int i = 0; i < targetActionVO.size(); i++) {
                targetActionVO.get(i).setStep(i + 1);
            }
            releaseMachineVO.setActions(targetActionVO);
        }
        return detailVO;
    }

    @Override
    public ReleaseBillLogVO releaseBillLog(Long id) {
        return null;
    }

}
