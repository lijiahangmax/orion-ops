package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.collect.Lists;
import com.orion.lang.utils.convert.Converts;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.event.EventKeys;
import com.orion.ops.consts.event.EventParamsHolder;
import com.orion.ops.dao.ApplicationProfileDAO;
import com.orion.ops.entity.domain.ApplicationProfileDO;
import com.orion.ops.entity.dto.ApplicationProfileDTO;
import com.orion.ops.entity.request.ApplicationProfileRequest;
import com.orion.ops.entity.vo.ApplicationProfileFastVO;
import com.orion.ops.entity.vo.ApplicationProfileVO;
import com.orion.ops.service.api.*;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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

    @Resource
    private ApplicationMachineService applicationMachineService;

    @Resource
    private ApplicationEnvService applicationEnvService;

    @Resource
    private ApplicationActionService applicationActionService;

    @Resource
    private ApplicationPipelineService applicationPipelineService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Long addProfile(ApplicationProfileRequest request) {
        String name = request.getName();
        String tag = request.getTag();
        // 重复名称检查
        this.checkNamePresent(null, name);
        // 重复标识检查
        this.checkTagPresent(null, tag);
        // 插入
        ApplicationProfileDO insert = new ApplicationProfileDO();
        insert.setProfileName(name);
        insert.setProfileTag(tag);
        insert.setDescription(request.getDescription());
        insert.setReleaseAudit(request.getReleaseAudit());
        applicationProfileDAO.insert(insert);
        Long id = insert.getId();
        // 插入缓存
        this.setProfileToCache(insert);
        // 设置日志参数
        EventParamsHolder.addParams(insert);
        return id;
    }

    @Override
    public Integer updateProfile(ApplicationProfileRequest request) {
        // 检查是否存在
        Long id = request.getId();
        ApplicationProfileDO beforeProfile = applicationProfileDAO.selectById(id);
        Valid.notNull(beforeProfile, MessageConst.PROFILE_ABSENT);
        String name = request.getName();
        String tag = request.getTag();
        // 重复名称检查
        this.checkNamePresent(id, name);
        // 重复标识检查
        this.checkTagPresent(id, tag);
        // 修改
        ApplicationProfileDO update = new ApplicationProfileDO();
        update.setId(id);
        update.setProfileName(name);
        update.setProfileTag(tag);
        update.setDescription(request.getDescription());
        update.setReleaseAudit(request.getReleaseAudit());
        update.setUpdateTime(new Date());
        int updateEffect = applicationProfileDAO.updateById(update);
        // 修改缓存
        this.setProfileToCache(update);
        // 设置日志参数
        EventParamsHolder.addParams(update);
        EventParamsHolder.addParam(EventKeys.NAME, beforeProfile.getProfileName());
        return updateEffect;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteProfile(Long id) {
        // 检查是否存在
        ApplicationProfileDO beforeProfile = applicationProfileDAO.selectById(id);
        Valid.notNull(beforeProfile, MessageConst.PROFILE_ABSENT);
        int effect = 0;
        // 删除环境
        effect += applicationProfileDAO.deleteById(id);
        // 删除环境变量
        effect += applicationEnvService.deleteAppProfileEnvByAppProfileId(null, id);
        // 删除环境机器
        effect += applicationMachineService.deleteAppMachineByAppProfileId(null, id);
        // 删除环境构建发布流程
        effect += applicationActionService.deleteAppActionByAppProfileId(null, id);
        // 删除应用流水线
        effect += applicationPipelineService.deleteByProfileId(id);
        // 删除缓存
        redisTemplate.opsForHash().delete(KeyConst.DATA_PROFILE_KEY, id.toString());
        // 设置日志参数
        EventParamsHolder.addParam(EventKeys.ID, id);
        EventParamsHolder.addParam(EventKeys.NAME, beforeProfile.getProfileName());
        return effect;
    }

    @Override
    public List<ApplicationProfileVO> listProfiles(ApplicationProfileRequest request) {
        LambdaQueryWrapper<ApplicationProfileDO> wrapper = new LambdaQueryWrapper<ApplicationProfileDO>()
                .like(!Strings.isBlank(request.getName()), ApplicationProfileDO::getProfileName, request.getName())
                .like(!Strings.isBlank(request.getTag()), ApplicationProfileDO::getProfileTag, request.getTag())
                .like(!Strings.isBlank(request.getDescription()), ApplicationProfileDO::getDescription, request.getDescription());
        List<ApplicationProfileDO> profileList = applicationProfileDAO.selectList(wrapper);
        return Converts.toList(profileList, ApplicationProfileVO.class);
    }

    @Override
    public List<ApplicationProfileFastVO> fastListProfiles() {
        List<ApplicationProfileFastVO> list = Lists.newList();
        // 查询缓存
        List<Object> profileCache = redisTemplate.opsForHash().values(KeyConst.DATA_PROFILE_KEY);
        if (Lists.isEmpty(profileCache)) {
            // 查库
            List<ApplicationProfileDTO> profiles = applicationProfileDAO.selectList(null)
                    .stream()
                    .map(s -> Converts.to(s, ApplicationProfileDTO.class))
                    .collect(Collectors.toList());
            // 设置缓存
            Map<String, String> cacheData = profiles.stream()
                    .collect(Collectors.toMap(s -> s.getId().toString(), JSON::toJSONString));
            redisTemplate.opsForHash().putAll(KeyConst.DATA_PROFILE_KEY, cacheData);
            redisTemplate.expire(KeyConst.DATA_PROFILE_KEY, KeyConst.DATA_PROFILE_EXPIRE, TimeUnit.SECONDS);
            // 返回
            profiles.stream().map(p -> Converts.to(p, ApplicationProfileFastVO.class))
                    .forEach(list::add);
        } else {
            // 返回
            profileCache.stream().map(p -> JSON.parseObject(p.toString(), ApplicationProfileDTO.class))
                    .map(p -> Converts.to(p, ApplicationProfileFastVO.class))
                    .forEach(list::add);
            // 排序
            list.sort(Comparator.comparing(ApplicationProfileFastVO::getId));
        }
        return list;
    }

    @Override
    public ApplicationProfileVO getProfile(Long id) {
        ApplicationProfileDO profile = applicationProfileDAO.selectById(id);
        Valid.notNull(profile, MessageConst.UNKNOWN_DATA);
        return Converts.to(profile, ApplicationProfileVO.class);
    }

    @Override
    public void clearProfileCache() {
        redisTemplate.delete(KeyConst.DATA_PROFILE_KEY);
    }

    /**
     * 检查名称是否存在
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

    /**
     * 检查唯一标识是否存在
     *
     * @param id  id
     * @param tag tag
     */
    private void checkTagPresent(Long id, String tag) {
        LambdaQueryWrapper<ApplicationProfileDO> presentWrapper = new LambdaQueryWrapper<ApplicationProfileDO>()
                .ne(id != null, ApplicationProfileDO::getId, id)
                .eq(ApplicationProfileDO::getProfileTag, tag);
        boolean present = DataQuery.of(applicationProfileDAO).wrapper(presentWrapper).present();
        Valid.isTrue(!present, MessageConst.TAG_PRESENT);
    }

    /**
     * 设置环境缓存
     *
     * @param profile profile
     */
    private void setProfileToCache(ApplicationProfileDO profile) {
        ApplicationProfileDTO profileDTO = Converts.to(profile, ApplicationProfileDTO.class);
        redisTemplate.opsForHash().put(KeyConst.DATA_PROFILE_KEY, profile.getId().toString(), JSON.toJSONString(profileDTO));
        redisTemplate.expire(KeyConst.DATA_PROFILE_KEY, KeyConst.DATA_PROFILE_EXPIRE, TimeUnit.SECONDS);
    }

}
