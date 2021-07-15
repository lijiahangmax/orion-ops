package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.dao.ApplicationProfileDAO;
import com.orion.ops.entity.domain.ApplicationProfileDO;
import com.orion.ops.entity.request.ApplicationProfileRequest;
import com.orion.ops.entity.vo.ApplicationProfileVO;
import com.orion.ops.service.api.ApplicationDeployActionService;
import com.orion.ops.service.api.ApplicationEnvService;
import com.orion.ops.service.api.ApplicationMachineService;
import com.orion.ops.service.api.ApplicationProfileService;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
import com.orion.utils.Strings;
import com.orion.utils.convert.Converts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 应用环境服务实现
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/2 17:52
 */
@Service("applicationProfileService")
public class ApplicationProfileServiceImpl implements ApplicationProfileService {

    @Resource
    private ApplicationProfileDAO applicationProfileDAO;

    @Resource
    private ApplicationMachineService applicationMachineService;

    @Resource
    private ApplicationEnvService applicationEnvService;

    @Resource
    private ApplicationDeployActionService applicationDeployActionService;

    @Override
    public Long addProfile(ApplicationProfileRequest request) {
        String name = request.getName();
        // 重复检查
        this.checkNamePresent(null, name);
        // 插入
        ApplicationProfileDO insert = new ApplicationProfileDO();
        insert.setProfileName(request.getName());
        insert.setProfileTag(request.getTag());
        insert.setDescription(request.getDescription());
        insert.setReleaseAudit(request.getReleaseAudit());
        applicationProfileDAO.insert(insert);
        return insert.getId();
    }

    @Override
    public Integer updateProfile(ApplicationProfileRequest request) {
        Long id = request.getId();
        String name = request.getName();
        // 重复检查
        this.checkNamePresent(id, name);
        // 修改
        ApplicationProfileDO update = new ApplicationProfileDO();
        update.setId(id);
        update.setProfileName(request.getName());
        update.setProfileTag(request.getTag());
        update.setDescription(request.getDescription());
        update.setReleaseAudit(request.getReleaseAudit());
        update.setUpdateTime(new Date());
        return applicationProfileDAO.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteProfile(Long id) {
        int effect = 0;
        // 删除环境
        effect += applicationProfileDAO.deleteById(id);
        // 删除环境变量
        effect += applicationEnvService.deleteAppProfileEnvByAppProfileId(null, id);
        // 删除环境机器
        effect += applicationMachineService.deleteAppMachineByAppProfileId(null, id);
        // 删除环境部署步骤
        effect += applicationDeployActionService.deleteAppActionByAppProfileId(null, id);
        return effect;
    }

    @Override
    public List<ApplicationProfileVO> listProfile(ApplicationProfileRequest request) {
        LambdaQueryWrapper<ApplicationProfileDO> wrapper = new LambdaQueryWrapper<ApplicationProfileDO>()
                .like(!Strings.isBlank(request.getName()), ApplicationProfileDO::getProfileName, request.getName())
                .like(!Strings.isBlank(request.getTag()), ApplicationProfileDO::getProfileTag, request.getTag());
        List<ApplicationProfileDO> profileList = applicationProfileDAO.selectList(wrapper);
        return Converts.toList(profileList, ApplicationProfileVO.class);
    }

    /**
     * 检查是否存在
     *
     * @param id   id
     * @param name name
     */
    private void checkNamePresent(Long id, String name) {
        LambdaQueryWrapper<ApplicationProfileDO> presentWrapper = new LambdaQueryWrapper<ApplicationProfileDO>()
                .ne(id != null, ApplicationProfileDO::getId, id)
                .eq(ApplicationProfileDO::getProfileName, name);
        boolean present = DataQuery.of(applicationProfileDAO).wrapper(presentWrapper).present();
        Valid.isTrue(!present, MessageConst.NAME_PRESENT);
    }

}
