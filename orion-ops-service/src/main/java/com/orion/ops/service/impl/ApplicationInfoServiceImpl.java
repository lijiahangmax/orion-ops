package com.orion.ops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.dao.ApplicationInfoDAO;
import com.orion.ops.entity.domain.ApplicationInfoDO;
import com.orion.ops.entity.request.ApplicationInfoRequest;
import com.orion.ops.entity.vo.ApplicationInfoVO;
import com.orion.ops.service.api.ApplicationInfoService;
import com.orion.ops.utils.DataQuery;
import com.orion.ops.utils.Valid;
import com.orion.utils.Strings;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 应用服务实现
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/7/2 17:52
 */
@Service("applicationInfoService")
public class ApplicationInfoServiceImpl implements ApplicationInfoService {

    @Resource
    private ApplicationInfoDAO applicationInfoDAO;

    @Override
    public Long insertApp(ApplicationInfoRequest request) {
        // 检查是否存在
        String name = request.getName();
        String tag = request.getTag();
        // 重复检查
        this.checkPresent(null, name, tag);
        // 插入
        ApplicationInfoDO insert = new ApplicationInfoDO();
        insert.setAppName(name);
        insert.setAppTag(tag);
        insert.setAppSort(request.getSort());
        insert.setDescription(request.getDescription());
        applicationInfoDAO.insert(insert);
        return insert.getId();
    }

    @Override
    public Integer updateApp(ApplicationInfoRequest request) {
        Long id = request.getId();
        String name = request.getName();
        String tag = request.getTag();
        // 重复检查
        this.checkPresent(id, name, tag);
        // 更新
        ApplicationInfoDO update = new ApplicationInfoDO();
        update.setId(id);
        update.setAppName(name);
        update.setAppTag(tag);
        update.setAppSort(request.getSort());
        update.setDescription(request.getDescription());
        update.setUpdateTime(new Date());
        return applicationInfoDAO.updateById(update);
    }

    @Override
    public Integer updateAppSort(Long id, boolean incr) {
        // 查询原来的排序
        ApplicationInfoDO app = applicationInfoDAO.selectById(id);
        Valid.notNull(app, MessageConst.APP_MISSING);
        Integer beforeSort = app.getAppSort();
        // 查询下一个排序
        LambdaQueryWrapper<ApplicationInfoDO> wrapper;
        if (incr) {
            wrapper = new LambdaQueryWrapper<ApplicationInfoDO>()
                    .ne(ApplicationInfoDO::getId, id)
                    .le(ApplicationInfoDO::getAppSort, beforeSort)
                    .orderByDesc(ApplicationInfoDO::getAppSort)
                    .last(Const.LIMIT_1);
        } else {
            wrapper = new LambdaQueryWrapper<ApplicationInfoDO>()
                    .ne(ApplicationInfoDO::getId, id)
                    .ge(ApplicationInfoDO::getAppSort, beforeSort)
                    .orderByAsc(ApplicationInfoDO::getAppSort)
                    .last(Const.LIMIT_1);
        }
        // 查询需要交换的
        ApplicationInfoDO swapApp = applicationInfoDAO.selectOne(wrapper);
        if (swapApp == null) {
            return 0;
        }
        // 交换
        ApplicationInfoDO updateSwap = new ApplicationInfoDO();
        updateSwap.setId(swapApp.getId());
        updateSwap.setAppSort(beforeSort);
        applicationInfoDAO.updateById(updateSwap);
        // 更新
        Integer afterSort = swapApp.getAppSort();
        if (afterSort.equals(beforeSort)) {
            if (incr) {
                afterSort -= 1;
            } else {
                afterSort += 1;
            }
        }
        ApplicationInfoDO updateTarget = new ApplicationInfoDO();
        updateTarget.setId(id);
        updateTarget.setAppSort(afterSort);
        return applicationInfoDAO.updateById(updateTarget);
    }

    @Override
    public Integer deleteApp(Long id) {
        // 删除关联
        return applicationInfoDAO.deleteById(id);
    }

    @Override
    public DataGrid<ApplicationInfoVO> listApp(ApplicationInfoRequest request) {
        LambdaQueryWrapper<ApplicationInfoDO> wrapper = new LambdaQueryWrapper<ApplicationInfoDO>()
                .like(!Strings.isBlank(request.getName()), ApplicationInfoDO::getAppName, request.getName())
                .like(!Strings.isBlank(request.getTag()), ApplicationInfoDO::getAppTag, request.getTag())
                .orderByAsc(ApplicationInfoDO::getAppSort);
        return DataQuery.of(applicationInfoDAO)
                .page(request)
                .wrapper(wrapper)
                .dataGrid(ApplicationInfoVO.class);
    }

    /**
     * 检查是否存在
     *
     * @param id   id
     * @param name name
     * @param tag  tag
     */
    private void checkPresent(Long id, String name, String tag) {
        LambdaQueryWrapper<ApplicationInfoDO> presentWrapper = new LambdaQueryWrapper<ApplicationInfoDO>()
                .ne(id != null, ApplicationInfoDO::getId, id)
                .and(s -> s.eq(ApplicationInfoDO::getAppName, name)
                        .or()
                        .eq(ApplicationInfoDO::getAppTag, tag));
        boolean present = DataQuery.of(applicationInfoDAO).wrapper(presentWrapper).present();
        Valid.isTrue(!present, MessageConst.NAME_TAG_PRESENT);
    }

}
