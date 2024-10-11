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
package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Strings;
import com.orion.ops.constant.KeyConst;
import com.orion.ops.constant.MessageConst;
import com.orion.ops.entity.dto.user.UserDTO;
import com.orion.ops.entity.importer.DataImportDTO;
import com.orion.ops.service.api.DataImportService;
import com.orion.ops.utils.Currents;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 数据导入服务
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/5/26 17:07
 */
@Slf4j
@Service("dataImportService")
public class DataImportServiceImpl implements DataImportService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public DataImportDTO checkImportToken(String token) {
        UserDTO user = Currents.getUser();
        // 查询缓存
        String data = redisTemplate.opsForValue().get(Strings.format(KeyConst.DATA_IMPORT_TOKEN, user.getId(), token));
        if (Strings.isEmpty(data)) {
            throw Exceptions.argument(MessageConst.OPERATOR_TIMEOUT);
        }
        // 设置用户数据
        DataImportDTO importData = JSON.parseObject(data, DataImportDTO.class);
        importData.setUserId(user.getId());
        importData.setUserName(user.getUsername());
        importData.setImportTime(new Date());
        return importData;
    }

    @Override
    public void clearImportToken(String token) {
        redisTemplate.delete(Strings.format(KeyConst.DATA_IMPORT_TOKEN, Currents.getUserId(), token));
    }

}
