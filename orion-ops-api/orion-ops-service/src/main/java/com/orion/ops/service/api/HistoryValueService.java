/*
 * Copyright (c) 2021 - present Jiahang Li (ops.orionsec.cn ljh1553488six@139.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.orion.ops.service.api;

import com.orion.lang.define.wrapper.DataGrid;
import com.orion.ops.constant.history.HistoryOperator;
import com.orion.ops.constant.history.HistoryValueType;
import com.orion.ops.entity.request.history.HistoryValueRequest;
import com.orion.ops.entity.vo.history.HistoryValueVO;

/**
 * 历史值 api
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/6/9 17:17
 */
public interface HistoryValueService {

    /**
     * 添加历史值快照
     *
     * @param valueId      valueId
     * @param valueType    valueType
     * @param operatorType operatorType
     * @param beforeValue  beforeValue
     * @param afterValue   afterValue
     */
    void addHistory(Long valueId, HistoryValueType valueType, HistoryOperator operatorType, String beforeValue, String afterValue);

    /**
     * 值列表
     *
     * @param request request
     * @return rows
     */
    DataGrid<HistoryValueVO> list(HistoryValueRequest request);

    /**
     * 回滚
     *
     * @param id id
     */
    void rollback(Long id);

}
