package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.dao.ApplicationProfileDAO;
import com.orion.ops.entity.domain.ApplicationProfileDO;
import com.orion.ops.entity.request.ApplicationProfileRequest;
import com.orion.ops.entity.vo.ApplicationProfileVO;
import com.orion.ops.service.api.ApplicationProfileService;
import com.orion.ops.utils.Valid;
import com.orion.utils.Strings;
import com.orion.utils.convert.Converts;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public Long addProfile(ApplicationProfileRequest request) {
        String name = request.getName();
        String tag = request.getTag();
        // 重复检查
        LambdaQueryWrapper<ApplicationProfileDO> presentWrapper = new LambdaQueryWrapper<ApplicationProfileDO>()
                .eq(ApplicationProfileDO::getProfileName, name)
                .eq(ApplicationProfileDO::getProfileTag, tag);
        Integer count = applicationProfileDAO.selectCount(presentWrapper);
        Valid.eq(count, 0, MessageConst.NAME_TAG_PRESENT);
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
        String tag = request.getTag();
        // 重复检查
        LambdaQueryWrapper<ApplicationProfileDO> presentWrapper = new LambdaQueryWrapper<ApplicationProfileDO>()
                .ne(ApplicationProfileDO::getId, id)
                .eq(ApplicationProfileDO::getProfileName, name)
                .eq(ApplicationProfileDO::getProfileTag, tag);
        Integer count = applicationProfileDAO.selectCount(presentWrapper);
        Valid.eq(count, 0, MessageConst.NAME_TAG_PRESENT);
        // 修改
        ApplicationProfileDO update = new ApplicationProfileDO();
        update.setId(id);
        update.setProfileName(request.getName());
        update.setProfileTag(request.getTag());
        update.setDescription(request.getDescription());
        update.setReleaseAudit(request.getReleaseAudit());
        return applicationProfileDAO.updateById(update);
    }

    @Override
    public Integer deleteProfile(Long id) {
        // 删除关联
        return applicationProfileDAO.deleteById(id);
    }

    @Override
    public List<ApplicationProfileVO> listProfile(ApplicationProfileRequest request) {
        LambdaQueryWrapper<ApplicationProfileDO> wrapper = new LambdaQueryWrapper<ApplicationProfileDO>()
                .eq(!Strings.isBlank(request.getName()), ApplicationProfileDO::getProfileName, request.getName())
                .eq(!Strings.isBlank(request.getTag()), ApplicationProfileDO::getProfileTag, request.getTag());
        return applicationProfileDAO.selectList(wrapper).stream()
                .map(s -> Converts.to(s, ApplicationProfileVO.class))
                .collect(Collectors.toList());
    }

}
