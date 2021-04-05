package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
import com.orion.lang.wrapper.PageRequest;
import com.orion.ops.entity.domain.MachineProxyDO;
import com.orion.ops.entity.vo.MachineProxyVO;

/**
 * 代理服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/3 21:58
 */
public interface MachineProxyService {

    /**
     * 添加代理
     *
     * @param proxy proxy
     * @return id
     */
    Long addProxy(MachineProxyDO proxy);

    /**
     * 分页查询
     *
     * @param request request
     * @return rows
     */
    DataGrid<MachineProxyVO> listProxy(PageRequest request);

    /**
     * 修改代理
     *
     * @param proxy proxy
     * @return effect
     */
    Integer updateProxy(MachineProxyDO proxy);

    /**
     * 删除代理
     *
     * @param id id
     * @return effect
     */
    Integer deleteProxy(Long id);

}
