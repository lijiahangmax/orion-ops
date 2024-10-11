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
package com.orion.ops.handler.importer.checker;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.orion.lang.id.UUIds;
import com.orion.lang.utils.Exceptions;
import com.orion.lang.utils.Strings;
import com.orion.lang.utils.collect.Lists;
import com.orion.lang.utils.collect.Maps;
import com.orion.lang.utils.convert.Converts;
import com.orion.office.excel.Excels;
import com.orion.office.excel.reader.ExcelBeanReader;
import com.orion.ops.constant.ImportType;
import com.orion.ops.constant.KeyConst;
import com.orion.ops.entity.importer.BaseDataImportDTO;
import com.orion.ops.entity.importer.DataImportDTO;
import com.orion.ops.entity.vo.data.DataImportCheckRowVO;
import com.orion.ops.entity.vo.data.DataImportCheckVO;
import com.orion.ops.utils.Currents;
import com.orion.spring.SpringHolder;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 导入数据检查器 基类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 15:59
 */
public abstract class AbstractDataChecker<T extends BaseDataImportDTO, DO> implements IDataChecker {

    private static final RedisTemplate<String, String> redisTemplate = SpringHolder.getBean("redisTemplate");

    /**
     * 导入类型
     */
    private final ImportType importType;

    /**
     * workbook
     */
    private final Workbook workbook;

    public AbstractDataChecker(ImportType importType, Workbook workbook) {
        this.importType = importType;
        this.workbook = workbook;
    }

    @Override
    public DataImportCheckVO doCheck() {
        // 解析数据
        List<T> rows;
        try {
            rows = this.parserImportWorkbook();
        } catch (Exception e) {
            throw Exceptions.parse(e);
        } finally {
            Excels.close(workbook);
        }
        // 检查数据
        return this.checkImportData(rows);
    }

    /**
     * 检查数据
     *
     * @param rows rows
     * @return check
     */
    protected abstract DataImportCheckVO checkImportData(List<T> rows);

    /**
     * 解析导入数据
     *
     * @return rows
     */
    @SuppressWarnings("unchecked")
    protected List<T> parserImportWorkbook() {
        ExcelBeanReader<T> reader = new ExcelBeanReader<T>(workbook, workbook.getSheetAt(0), (Class<T>) importType.getImportClass());
        return reader.skip(2)
                .read()
                .getRows();
    }

    /**
     * 验证对象合法性
     *
     * @param rows rows
     */
    protected void validImportRows(List<T> rows) {
        for (T row : rows) {
            try {
                importType.getValidator().validData(row);
            } catch (Exception e) {
                row.setIllegalMessage(e.getMessage());
            }
        }
    }

    /**
     * 获取导入行已存在的数据
     *
     * @param rows            rows
     * @param rowKeyGetter    rowKeyGetter
     * @param mapper          mapper
     * @param domainKeyGetter domainKeyGetter
     * @return present domain
     */
    protected List<DO> getImportRowsPresentValues(List<T> rows,
                                                  Function<T, ?> rowKeyGetter,
                                                  BaseMapper<DO> mapper,
                                                  SFunction<DO, ?> domainKeyGetter) {
        List<?> symbolList = rows.stream()
                .filter(s -> Objects.isNull(s.getIllegalMessage()))
                .map(rowKeyGetter)
                .collect(Collectors.toList());
        if (symbolList.isEmpty()) {
            return Lists.empty();
        } else {
            LambdaQueryWrapper<DO> wrapper = new LambdaQueryWrapper<DO>()
                    .in(domainKeyGetter, symbolList);
            return mapper.selectList(wrapper);
        }
    }

    /**
     * 检查导入行是否存在
     *
     * @param rows             rows
     * @param rowKeyGetter     rowKeyGetter
     * @param presentValues    presentValues
     * @param presentKeyGetter presentKeyGetter
     * @param presentIdGetter  presentIdGetter
     */
    protected void checkImportRowsPresent(List<T> rows,
                                          Function<T, ?> rowKeyGetter,
                                          List<DO> presentValues,
                                          Function<DO, ?> presentKeyGetter,
                                          Function<DO, Long> presentIdGetter) {
        for (T row : rows) {
            presentValues.stream()
                    .filter(p -> presentKeyGetter.apply(p).equals(rowKeyGetter.apply(row)))
                    .findFirst()
                    .map(presentIdGetter)
                    .ifPresent(row::setId);
        }
    }

    /**
     * 设置检查行数据缓存
     *
     * @param rows rows
     * @return checkData
     */
    protected DataImportCheckVO setImportCheckRows(List<T> rows) {
        // 设置检查对象
        String dataJson = JSON.toJSONString(rows);
        List<DataImportCheckRowVO> illegalRows = Lists.newList();
        List<DataImportCheckRowVO> insertRows = Lists.newList();
        List<DataImportCheckRowVO> updateRows = Lists.newList();
        // 设置行
        for (int i = 0; i < rows.size(); i++) {
            // 设置检查数据
            T row = rows.get(i);
            DataImportCheckRowVO checkRow = Converts.to(row, DataImportCheckRowVO.class);
            checkRow.setIndex(i);
            checkRow.setRow(i + 3);
            // 检查非法数据
            if (checkRow.getIllegalMessage() != null) {
                illegalRows.add(checkRow);
                continue;
            }
            if (checkRow.getId() == null) {
                // 不存在则新增
                insertRows.add(checkRow);
            } else {
                // 存在则修改
                updateRows.add(checkRow);
            }
        }
        // 设置缓存并且返回
        String token = UUIds.random32();
        String cacheKey = Strings.format(KeyConst.DATA_IMPORT_TOKEN, Currents.getUserId(), token);
        // 返回数据
        DataImportCheckVO check = new DataImportCheckVO();
        check.setImportToken(token);
        check.setIllegalRows(illegalRows);
        check.setInsertRows(insertRows);
        check.setUpdateRows(updateRows);
        // 设置缓存
        DataImportDTO cache = new DataImportDTO();
        cache.setImportToken(token);
        cache.setType(importType.getType());
        cache.setData(dataJson);
        cache.setCheck(check);
        redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(cache),
                KeyConst.DATA_IMPORT_TOKEN_EXPIRE, TimeUnit.SECONDS);
        return check;
    }

    /**
     * 设置引用id
     *
     * @param rows               rows
     * @param keyGetter          keyGetter
     * @param query              dataQuery
     * @param domainKeyGetter    domainKeyGetter
     * @param domainIdGetter     domainIdGetter
     * @param relIdSetter        relIdSetter
     * @param notPresentTemplate notPresentTemplate
     * @param <K>                key type
     * @param <P>                domain type
     */
    protected <K, P> void setCheckRowsRelId(List<T> rows,
                                            Function<T, K> keyGetter,
                                            Function<List<K>, List<P>> query,
                                            Function<P, K> domainKeyGetter,
                                            Function<P, Long> domainIdGetter,
                                            BiConsumer<T, Long> relIdSetter,
                                            String notPresentTemplate) {
        // 获取合法数据
        List<T> validImportList = rows.stream()
                .filter(s -> Objects.isNull(s.getIllegalMessage()))
                .filter(s -> {
                    K symbol = keyGetter.apply(s);
                    return symbol instanceof String ? Strings.isNotBlank((String) symbol) : Objects.nonNull(symbol);
                }).collect(Collectors.toList());
        if (validImportList.isEmpty()) {
            return;
        }
        // 获取标识
        List<K> symbolList = validImportList.stream()
                .map(keyGetter)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (symbolList.isEmpty()) {
            return;
        }
        // 查询id
        Map<K, Long> symbolIdMap = Maps.newMap();
        List<P> dataList = query.apply(symbolList);
        dataList.forEach(s -> symbolIdMap.put(domainKeyGetter.apply(s), domainIdGetter.apply(s)));
        // 设置id
        for (T row : validImportList) {
            K symbol = keyGetter.apply(row);
            Long relId = symbolIdMap.get(symbol);
            if (relId == null) {
                row.setIllegalMessage(Strings.format(notPresentTemplate, symbol));
                continue;
            }
            relIdSetter.accept(row, relId);
        }
    }

}
