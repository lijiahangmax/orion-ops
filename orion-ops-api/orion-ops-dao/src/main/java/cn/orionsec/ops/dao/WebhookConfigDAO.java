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
package cn.orionsec.ops.dao;

import cn.orionsec.ops.entity.domain.WebhookConfigDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * webhook 配置 Mapper 接口
 * </p>
 *
 * @author Jiahang Li
 * @since 2022-08-23
 */
public interface WebhookConfigDAO extends BaseMapper<WebhookConfigDO> {

}
