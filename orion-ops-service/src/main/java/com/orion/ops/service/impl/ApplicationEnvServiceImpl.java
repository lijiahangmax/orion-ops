package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.collect.MutableLinkedHashMap;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.HistoryValueType;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.app.ApplicationEnvAttr;
import com.orion.ops.dao.ApplicationEnvDAO;
import com.orion.ops.dao.ApplicationInfoDAO;
import com.orion.ops.dao.ApplicationProfileDAO;
import com.orion.ops.entity.domain.ApplicationEnvDO;
import com.orion.ops.entity.request.ApplicationEnvRequest;
import com.orion.ops.entity.vo.ApplicationEnvVO;
import com.orion.ops.service.api.ApplicationEnvService;
import com.orion.ops.service.api.HistoryValueService;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
import com.orion.utils.Arrays1;
import com.orion.utils.Strings;
import com.orion.utils.collect.Maps;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * app环境 实现
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/4 11:23
 */
@Service("applicationEnvService")
public class ApplicationEnvServiceImpl implements ApplicationEnvService {

    @Resource
    private ApplicationInfoDAO applicationInfoDAO;

    @Resource
    private ApplicationProfileDAO applicationProfileDAO;

    @Resource
    private ApplicationEnvDAO applicationEnvDAO;

    @Resource
    private HistoryValueService historyValueService;

    @Override
    public Long addAppEnv(ApplicationEnvRequest request) {
        // 数据检查
        Long appId = request.getAppId();
        Long profileId = request.getProfileId();
        String key = request.getKey();
        Valid.notNull(applicationInfoDAO.selectById(appId), MessageConst.APP_ABSENT);
        Valid.notNull(applicationProfileDAO.selectById(profileId), MessageConst.PROFILE_ABSENT);
        // 重复检查
        LambdaQueryWrapper<ApplicationEnvDO> wrapper = new LambdaQueryWrapper<ApplicationEnvDO>()
                .eq(ApplicationEnvDO::getAppId, appId)
                .eq(ApplicationEnvDO::getProfileId, profileId)
                .eq(ApplicationEnvDO::getAttrKey, key)
                .last(Const.LIMIT_1);
        ApplicationEnvDO env = applicationEnvDAO.selectOne(wrapper);
        if (env != null) {
            // 修改
            Long id = env.getId();
            request.setId(id);
            this.updateAppEnv(request);
            return id;
        }
        // 新增
        ApplicationEnvDO entity = new ApplicationEnvDO();
        entity.setAppId(appId);
        entity.setProfileId(profileId);
        entity.setAttrKey(key.trim());
        entity.setAttrValue(request.getValue());
        entity.setDescription(request.getDescription());
        applicationEnvDAO.insert(entity);
        return entity.getId();
    }

    @Override
    public Integer updateAppEnv(ApplicationEnvRequest request) {
        // 查询
        Long id = request.getId();
        ApplicationEnvDO before = applicationEnvDAO.selectById(id);
        Valid.notNull(before, MessageConst.ENV_ABSENT);
        // 检查是否修改了值
        String value = request.getValue();
        String beforeValue = before.getAttrValue();
        if (!Strings.isBlank(value) && !value.equals(beforeValue)) {
            historyValueService.addHistory(id, HistoryValueType.APP_ENV, beforeValue);
        }
        // 更新
        ApplicationEnvDO update = new ApplicationEnvDO();
        update.setId(id);
        update.setAttrValue(value);
        update.setDescription(request.getDescription());
        update.setUpdateTime(new Date());
        return applicationEnvDAO.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteAppEnv(List<Long> idList) {
        int effect = 0;
        for (Long id : idList) {
            ApplicationEnvDO env = applicationEnvDAO.selectById(id);
            Valid.notNull(env, MessageConst.ENV_ABSENT);
            String key = env.getAttrKey();
            Valid.isTrue(ApplicationEnvAttr.of(key) == null, "{} " + MessageConst.FORBID_DELETE, key);
            effect += applicationEnvDAO.deleteById(id);
        }
        return effect;
    }

    @Override
    public DataGrid<ApplicationEnvVO> listAppEnv(ApplicationEnvRequest request) {
        LambdaQueryWrapper<ApplicationEnvDO> wrapper = new LambdaQueryWrapper<ApplicationEnvDO>()
                .like(Strings.isNotBlank(request.getKey()), ApplicationEnvDO::getAttrKey, request.getKey())
                .eq(ApplicationEnvDO::getAppId, request.getAppId())
                .eq(ApplicationEnvDO::getProfileId, request.getProfileId())
                .orderByAsc(ApplicationEnvDO::getId);
        return DataQuery.of(applicationEnvDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(ApplicationEnvVO.class);
    }

    @Override
    public String getAppEnvValue(Long appId, Long profileId, String key) {
        LambdaQueryWrapper<ApplicationEnvDO> wrapper = new LambdaQueryWrapper<ApplicationEnvDO>()
                .eq(ApplicationEnvDO::getAppId, appId)
                .eq(ApplicationEnvDO::getProfileId, profileId)
                .eq(ApplicationEnvDO::getAttrKey, key)
                .last(Const.LIMIT_1);
        return Optional.ofNullable(applicationEnvDAO.selectOne(wrapper))
                .map(ApplicationEnvDO::getAttrValue)
                .orElse(null);
    }

    @Override
    public MutableLinkedHashMap<String, String> getAppProfileEnv(Long appId, Long profileId) {
        MutableLinkedHashMap<String, String> env = Maps.newLinkedMutableMap();
        LambdaQueryWrapper<ApplicationEnvDO> wrapper = new LambdaQueryWrapper<ApplicationEnvDO>()
                .eq(ApplicationEnvDO::getAppId, appId)
                .eq(ApplicationEnvDO::getProfileId, profileId)
                .orderByAsc(ApplicationEnvDO::getId);
        applicationEnvDAO.selectList(wrapper).forEach(e -> env.put(e.getAttrKey(), e.getAttrValue()));
        return env;
    }

    @Override
    public Integer deleteAppProfileEnvByAppProfileId(Long appId, Long profileId, Object... envKeys) {
        LambdaQueryWrapper<ApplicationEnvDO> wrapper = new LambdaQueryWrapper<ApplicationEnvDO>()
                .eq(appId != null, ApplicationEnvDO::getAppId, appId)
                .eq(profileId != null, ApplicationEnvDO::getProfileId, profileId)
                .in(!Arrays1.isEmpty(envKeys), ApplicationEnvDO::getAttrKey, envKeys);
        return applicationEnvDAO.delete(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncAppProfileEnv(Long appId, Long profileId, Long syncProfileId) {
        // 查询
        LambdaQueryWrapper<ApplicationEnvDO> querySourceWrapper = new LambdaQueryWrapper<ApplicationEnvDO>()
                .eq(ApplicationEnvDO::getAppId, appId)
                .eq(ApplicationEnvDO::getProfileId, profileId);
        List<ApplicationEnvDO> sourceEnvs = applicationEnvDAO.selectList(querySourceWrapper);
        // 更新
        for (ApplicationEnvDO sourceEnv : sourceEnvs) {
            ApplicationEnvRequest update = new ApplicationEnvRequest();
            update.setAppId(appId);
            update.setProfileId(syncProfileId);
            update.setKey(sourceEnv.getAttrKey());
            update.setValue(sourceEnv.getAttrValue());
            update.setDescription(sourceEnv.getDescription());
            this.addAppEnv(update);
        }
        // 删除
        List<String> sourceKeys = sourceEnvs.stream()
                .map(ApplicationEnvDO::getAttrKey)
                .collect(Collectors.toList());
        LambdaQueryWrapper<ApplicationEnvDO> deleteWrapper = new LambdaQueryWrapper<ApplicationEnvDO>()
                .eq(ApplicationEnvDO::getAppId, appId)
                .eq(ApplicationEnvDO::getProfileId, syncProfileId)
                .notIn(ApplicationEnvDO::getAttrKey, sourceKeys);
        applicationEnvDAO.delete(deleteWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyAppEnv(Long appId, Long targetAppId) {
        LambdaQueryWrapper<ApplicationEnvDO> wrapper = new LambdaQueryWrapper<ApplicationEnvDO>()
                .eq(ApplicationEnvDO::getAppId, appId);
        List<ApplicationEnvDO> envs = applicationEnvDAO.selectList(wrapper);
        envs.forEach(s -> {
            s.setId(null);
            s.setAppId(targetAppId);
            s.setCreateTime(null);
            s.setUpdateTime(null);
        });
        envs.forEach(applicationEnvDAO::insert);
    }

}
