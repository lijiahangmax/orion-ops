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
package com.orion.ops.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.lang.define.wrapper.DataGrid;
import com.orion.lang.define.wrapper.PageRequest;
import com.orion.lang.define.wrapper.Pager;
import com.orion.lang.utils.Valid;
import com.orion.lang.utils.collect.Lists;
import com.orion.lang.utils.convert.Converts;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 数据查询器
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/5/27 17:22
 */
public class DataQuery<T> {

    private final BaseMapper<T> dao;

    private PageRequest page;

    private LambdaQueryWrapper<T> wrapper;

    private DataQuery(BaseMapper<T> dao) {
        this.dao = dao;
    }

    public static <T> DataQuery<T> of(BaseMapper<T> dao) {
        Valid.notNull(dao, "dao is null");
        return new DataQuery<>(dao);
    }

    public DataQuery<T> page(PageRequest page) {
        this.page = Valid.notNull(page, "page is null");
        return this;
    }

    public DataQuery<T> wrapper(LambdaQueryWrapper<T> wrapper) {
        this.wrapper = Valid.notNull(wrapper, "wrapper is null");
        return this;
    }

    public Optional<T> get() {
        return Optional.ofNullable(dao.selectOne(wrapper));
    }

    public <R> Optional<R> get(Class<R> c) {
        return Optional.ofNullable(dao.selectOne(wrapper))
                .map(s -> Converts.to(s, c));
    }

    public Stream<T> list() {
        return dao.selectList(wrapper).stream();
    }

    public <R> List<R> list(Class<R> c) {
        return Converts.toList(dao.selectList(wrapper), c);
    }

    public Integer count() {
        return dao.selectCount(wrapper);
    }

    public boolean present() {
        return dao.selectCount(wrapper) > 0;
    }

    public DataGrid<T> dataGrid() {
        return this.dataGrid(Function.identity());
    }

    public <R> DataGrid<R> dataGrid(Class<R> c) {
        return this.dataGrid(t -> Converts.to(t, c));
    }

    public <R> DataGrid<R> dataGrid(Function<T, R> convert) {
        Valid.notNull(convert, "convert is null");
        Valid.notNull(page, "page is null");
        Valid.notNull(wrapper, "wrapper is null");
        Integer count = dao.selectCount(wrapper);
        Pager<R> pager = new Pager<>(page);
        pager.setTotal(count);
        boolean next = pager.hasMoreData();
        if (next) {
            wrapper.last(pager.getSql());
            List<R> rows = dao.selectList(wrapper).stream()
                    .map(convert)
                    .collect(Collectors.toList());
            pager.setRows(rows);
        } else {
            pager.setRows(Lists.empty());
        }
        return DataGrid.of(pager);
    }

}
