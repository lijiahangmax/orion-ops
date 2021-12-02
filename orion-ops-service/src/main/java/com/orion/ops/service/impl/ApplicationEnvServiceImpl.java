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
import com.orion.ops.entity.request.ApplicationConfigEnvRequest;
import com.orion.ops.entity.request.ApplicationConfigRequest;
import com.orion.ops.entity.request.ApplicationEnvRequest;
import com.orion.ops.entity.vo.ApplicationEnvVO;
import com.orion.ops.service.api.ApplicationEnvService;
import com.orion.ops.service.api.HistoryValueService;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
import com.orion.spring.SpringHolder;
import com.orion.utils.Strings;
import com.orion.utils.collect.Maps;
import com.orion.utils.convert.Converts;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    @Transactional(rollbackFor = Exception.class)
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
        ApplicationEnvDO insert = new ApplicationEnvDO();
        insert.setAppId(appId);
        insert.setProfileId(profileId);
        insert.setAttrKey(key.trim());
        insert.setAttrValue(request.getValue());
        insert.setDescription(request.getDescription());
        applicationEnvDAO.insert(insert);
        // 插入历史值
        Long id = insert.getId();
        historyValueService.addHistory(id, HistoryValueType.APP_ENV, Const.ADD, null, request.getValue());
        return id;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAddAppEnv(Long appId, Long profileId, Map<String, String> env) {
        ApplicationEnvService self = SpringHolder.getBean(ApplicationEnvService.class);
        env.forEach((k, v) -> {
            ApplicationEnvRequest request = new ApplicationEnvRequest();
            request.setAppId(appId);
            request.setProfileId(profileId);
            request.setKey(k);
            request.setValue(v);
            self.addAppEnv(request);
        });
    }

    @Override
    public Integer updateAppEnv(ApplicationEnvRequest request) {
        // 查询
        Long id = request.getId();
        ApplicationEnvDO before = applicationEnvDAO.selectById(id);
        Valid.notNull(before, MessageConst.ENV_ABSENT);
        // 检查是否修改了值
        String beforeValue = before.getAttrValue();
        String afterValue = request.getValue();
        if (afterValue != null && !afterValue.equals(beforeValue)) {
            historyValueService.addHistory(id, HistoryValueType.APP_ENV, Const.UPDATE, beforeValue, afterValue);
        }
        // 更新
        ApplicationEnvDO update = new ApplicationEnvDO();
        update.setId(id);
        update.setAttrValue(afterValue);
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
                .like(Strings.isNotBlank(request.getValue()), ApplicationEnvDO::getAttrValue, request.getValue())
                .like(Strings.isNotBlank(request.getDescription()), ApplicationEnvDO::getDescription, request.getDescription())
                .eq(ApplicationEnvDO::getAppId, request.getAppId())
                .eq(ApplicationEnvDO::getProfileId, request.getProfileId())
                .orderByAsc(ApplicationEnvDO::getId);
        return DataQuery.of(applicationEnvDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(ApplicationEnvVO.class);
    }

    @Override
    public ApplicationEnvVO getAppEnvDetail(Long id) {
        ApplicationEnvDO env = applicationEnvDAO.selectById(id);
        Valid.notNull(env, MessageConst.UNKNOWN_DATA);
        return Converts.to(env, ApplicationEnvVO.class);
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
        MutableLinkedHashMap<String, String> env = Maps.newMutableLinkedMap();
        LambdaQueryWrapper<ApplicationEnvDO> wrapper = new LambdaQueryWrapper<ApplicationEnvDO>()
                .eq(ApplicationEnvDO::getAppId, appId)
                .eq(ApplicationEnvDO::getProfileId, profileId)
                .orderByAsc(ApplicationEnvDO::getId);
        applicationEnvDAO.selectList(wrapper).forEach(e -> env.put(e.getAttrKey(), e.getAttrValue()));
        return env;
    }

    @Override
    public Integer deleteAppProfileEnvByAppProfileId(Long appId, Long profileId) {
        LambdaQueryWrapper<ApplicationEnvDO> wrapper = new LambdaQueryWrapper<ApplicationEnvDO>()
                .eq(appId != null, ApplicationEnvDO::getAppId, appId)
                .eq(profileId != null, ApplicationEnvDO::getProfileId, profileId);
        return applicationEnvDAO.delete(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void configAppEnv(ApplicationConfigRequest request) {
        ApplicationEnvService self = SpringHolder.getBean(ApplicationEnvService.class);
        Long appId = request.getAppId();
        Long profileId = request.getProfileId();
        ApplicationConfigEnvRequest env = request.getEnv();
        // 构建产物目录
        ApplicationEnvRequest bundlePath = new ApplicationEnvRequest();
        bundlePath.setAppId(appId);
        bundlePath.setProfileId(profileId);
        bundlePath.setKey(ApplicationEnvAttr.BUNDLE_PATH.getKey());
        bundlePath.setValue(env.getBundlePath());
        bundlePath.setDescription(ApplicationEnvAttr.BUNDLE_PATH.getDescription());
        self.addAppEnv(bundlePath);
        // 检查是否有构建序列
        String buildSeqValue = self.getAppEnvValue(appId, profileId, ApplicationEnvAttr.BUILD_SEQ.getKey());
        if (buildSeqValue == null) {
            // 构建序列
            ApplicationEnvRequest buildSeq = new ApplicationEnvRequest();
            buildSeq.setAppId(appId);
            buildSeq.setProfileId(profileId);
            buildSeq.setKey(ApplicationEnvAttr.BUILD_SEQ.getKey());
            buildSeq.setValue(0 + Strings.EMPTY);
            buildSeq.setDescription(ApplicationEnvAttr.BUILD_SEQ.getDescription());
            self.addAppEnv(buildSeq);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncAppProfileEnv(Long appId, Long profileId, Long targetProfileId) {
        // 删除
        LambdaQueryWrapper<ApplicationEnvDO> deleteWrapper = new LambdaQueryWrapper<ApplicationEnvDO>()
                .eq(ApplicationEnvDO::getAppId, appId)
                .eq(ApplicationEnvDO::getProfileId, targetProfileId);
        applicationEnvDAO.delete(deleteWrapper);
        // 查询
        LambdaQueryWrapper<ApplicationEnvDO> queryWrapper = new LambdaQueryWrapper<ApplicationEnvDO>()
                .eq(ApplicationEnvDO::getAppId, appId)
                .eq(ApplicationEnvDO::getProfileId, profileId);
        List<ApplicationEnvDO> sourceEnvList = applicationEnvDAO.selectList(queryWrapper);
        // 插入
        for (ApplicationEnvDO sourceEnv : sourceEnvList) {
            ApplicationEnvRequest update = new ApplicationEnvRequest();
            update.setAppId(appId);
            update.setProfileId(targetProfileId);
            update.setKey(sourceEnv.getAttrKey());
            update.setValue(sourceEnv.getAttrValue());
            update.setDescription(sourceEnv.getDescription());
            this.addAppEnv(update);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyAppEnv(Long appId, Long targetAppId) {
        LambdaQueryWrapper<ApplicationEnvDO> wrapper = new LambdaQueryWrapper<ApplicationEnvDO>()
                .eq(ApplicationEnvDO::getAppId, appId);
        List<ApplicationEnvDO> envList = applicationEnvDAO.selectList(wrapper);
        for (ApplicationEnvDO env : envList) {
            env.setId(null);
            env.setAppId(targetAppId);
            env.setCreateTime(null);
            env.setUpdateTime(null);
            applicationEnvDAO.insert(env);
        }
    }

}
