package com.orion.ops.service.api;

import com.orion.lang.collect.MutableLinkedHashMap;
import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.entity.domain.SystemEnvDO;
import com.orion.ops.entity.request.SystemEnvRequest;
import com.orion.ops.entity.vo.SystemEnvVO;

import java.util.List;
import java.util.Map;

/**
 * 系统环境变量服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/2/15 17:05
 */
public interface SystemEnvService {

    /**
     * 添加变量
     *
     * @param request request
     * @return id
     */
    Long addEnv(SystemEnvRequest request);

    /**
     * 修改变量
     *
     * @param request request
     * @return effect
     */
    Integer updateEnv(SystemEnvRequest request);

    /**
     * 修改变量
     *
     * @param before  before
     * @param request request
     * @return effect
     */
    Integer updateEnv(SystemEnvDO before, SystemEnvRequest request);

    /**
     * 通过id删除
     *
     * @param idList idList
     * @return effect
     */
    Integer deleteEnv(List<Long> idList);

    /**
     * 批量添加
     *
     * @param env env
     */
    void saveEnv(Map<String, String> env);

    /**
     * 列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<SystemEnvVO> listEnv(SystemEnvRequest request);

    /**
     * 详情
     *
     * @param id id
     * @return row
     */
    SystemEnvVO getEnvDetail(Long id);

    /**
     * 获取系统变量
     *
     * @param env envKey
     * @return env
     */
    String getEnvValue(String env);

    /**
     * 通过名称获取
     *
     * @param env env
     * @return env
     */
    SystemEnvDO selectByName(String env);

    /**
     * 获取系统环境变量
     *
     * @return map
     */
    MutableLinkedHashMap<String, String> getSystemEnv();

    /**
     * 获取系统环境变量
     *
     * @return map
     */
    MutableLinkedHashMap<String, String> getFullSystemEnv();

}
