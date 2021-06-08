package com.orion.ops.service.api;

import com.orion.lang.wrapper.DataGrid;
import com.orion.ops.entity.request.MachineProxyRequest;
import com.orion.ops.entity.vo.MachineProxyVO;

import java.util.List;

/**
 * 代理服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/4/3 21:58
 */
public interface MachineProxyService {

    /**
     * 添加/修改 代理
     *
     * @param request request
     * @return id/effect
     */
    Long addUpdateProxy(MachineProxyRequest request);

    /**
     * 分页查询
     *
     * @param request request
     * @return rows
     */
    DataGrid<MachineProxyVO> listProxy(MachineProxyRequest request);

    /**
     * 删除代理
     *
     * @param idList idList
     * @return effect
     */
    Integer deleteProxy(List<Long> idList);

}
