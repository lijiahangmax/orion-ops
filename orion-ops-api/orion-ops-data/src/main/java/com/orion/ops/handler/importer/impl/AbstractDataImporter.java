package com.orion.ops.handler.importer.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.orion.lang.utils.Threads;
import com.orion.lang.utils.Valid;
import com.orion.lang.utils.collect.Maps;
import com.orion.lang.utils.convert.Converts;
import com.orion.lang.utils.io.FileWriters;
import com.orion.lang.utils.io.Files1;
import com.orion.lang.utils.time.Dates;
import com.orion.ops.constant.ImportType;
import com.orion.ops.constant.SchedulerPools;
import com.orion.ops.constant.event.EventKeys;
import com.orion.ops.constant.message.MessageType;
import com.orion.ops.constant.system.SystemEnvAttr;
import com.orion.ops.entity.importer.BaseDataImportDTO;
import com.orion.ops.entity.importer.DataImportDTO;
import com.orion.ops.entity.vo.data.DataImportCheckRowVO;
import com.orion.ops.entity.vo.data.DataImportCheckVO;
import com.orion.ops.service.api.DataImportService;
import com.orion.ops.service.api.WebSideMessageService;
import com.orion.ops.utils.PathBuilders;
import com.orion.spring.SpringHolder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据导入器 基类
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2022/9/9 16:51
 */
@Slf4j
public abstract class AbstractDataImporter<DO> implements IDataImporter {

    private static final DataImportService dataImportService = SpringHolder.getBean(DataImportService.class);

    private static final WebSideMessageService webSideMessageService = SpringHolder.getBean(WebSideMessageService.class);

    /**
     * 导入数据
     */
    protected final DataImportDTO importData;

    /**
     * domain mapper
     */
    protected final BaseMapper<DO> mapper;

    /**
     * 导入类型
     */
    protected ImportType importType;

    public AbstractDataImporter(DataImportDTO importData, BaseMapper<DO> mapper) {
        this.importData = importData;
        this.mapper = mapper;
    }

    @Override
    public void doImport() {
        // 异步执行导入
        Threads.start(this::doImportData, SchedulerPools.ASYNC_IMPORT_SCHEDULER);
    }

    /**
     * 执行导入数据
     */
    @SuppressWarnings("unchecked")
    public void doImportData() {
        this.importType = ImportType.of(importData.getType());
        Exception ex = null;
        try {
            // 获取缓存数据
            DataImportCheckVO dataCheck = importData.getCheck();
            List<BaseDataImportDTO> rows = this.getImportData();
            // 插入
            this.getImportInsertData(dataCheck, rows, (Class<DO>) importType.getConvertClass())
                    .stream()
                    .map(s -> (DO) s)
                    .peek(this::insertFiller)
                    .peek(mapper::insert)
                    .forEach(this::insertCallback);
            // 更新
            this.getImportUpdateData(dataCheck, rows, (Class<DO>) importType.getConvertClass())
                    .stream()
                    .map(s -> (DO) s)
                    .peek(this::updateFiller)
                    .peek(mapper::updateById)
                    .forEach(this::updateCallback);
        } catch (Exception e) {
            ex = e;
            log.error("{}导入失败 token: {}, data: {}", importType.name(), importData.getImportToken(), JSON.toJSONString(importData), e);
        }
        // 发送站内信
        this.sendImportWebSideMessage(ex == null);
        // 保存日志
        this.saveImportDataJson(importData);
        // 导入完成回调
        this.importFinishCallback(ex == null);
    }

    /**
     * 数据插入填充
     *
     * @param row row
     */
    protected void insertFiller(DO row) {
    }

    /**
     * 数据修改填充
     *
     * @param row row
     */
    protected void updateFiller(DO row) {
    }

    /**
     * 数据插入回调
     *
     * @param row row
     */
    protected void insertCallback(DO row) {
    }

    /**
     * 数据更新回调
     *
     * @param row row
     */
    protected void updateCallback(DO row) {
    }

    /**
     * 导入完成回调
     *
     * @param isSuccess 是否成功
     */
    protected void importFinishCallback(boolean isSuccess) {
    }

    /**
     * 获取导入数据
     *
     * @param <T> T
     * @return list
     */
    @SuppressWarnings("unchecked")
    private <T extends BaseDataImportDTO> List<T> getImportData() {
        ImportType type = ImportType.of(importData.getType());
        Valid.notNull(type);
        return (List<T>) JSON.parseArray(importData.getData(), type.getImportClass());
    }

    /**
     * 获取导入插入数据
     *
     * @param dataCheck    dataCheck
     * @param dataList     list
     * @param convertClass convertClass
     * @param <T>          T
     * @return rows
     */
    private <T extends BaseDataImportDTO> List<DO> getImportInsertData(DataImportCheckVO dataCheck,
                                                                       List<T> dataList,
                                                                       Class<DO> convertClass) {
        return dataCheck.getInsertRows().stream()
                .map(DataImportCheckRowVO::getIndex)
                .map(dataList::get)
                .map(s -> Converts.to(s, convertClass))
                .collect(Collectors.toList());
    }

    /**
     * 获取导入更新数据
     *
     * @param dataCheck    dataCheck
     * @param dataList     list
     * @param convertClass convertClass
     * @param <T>          T
     * @return rows
     */
    private <T extends BaseDataImportDTO> List<DO> getImportUpdateData(DataImportCheckVO dataCheck,
                                                                       List<T> dataList,
                                                                       Class<DO> convertClass) {
        return dataCheck.getUpdateRows().stream()
                .map(DataImportCheckRowVO::getIndex)
                .map(dataList::get)
                .map(s -> Converts.to(s, convertClass))
                .collect(Collectors.toList());
    }

    /**
     * 发送站内信
     *
     * @param isSuccess 是否导入成功
     */
    private void sendImportWebSideMessage(boolean isSuccess) {
        // 消息类型
        MessageType messageType;
        if (isSuccess) {
            messageType = MessageType.DATA_IMPORT_SUCCESS;
        } else {
            messageType = MessageType.DATA_IMPORT_FAILURE;
        }
        // 站内信参数
        Map<String, Object> params = Maps.newMap();
        params.put(EventKeys.TIME, Dates.format(importData.getImportTime()));
        params.put(EventKeys.TOKEN, importData.getImportToken());
        params.put(EventKeys.LABEL, importType.getLabel());
        webSideMessageService.addMessage(messageType, importData.getType().longValue(), importData.getUserId(), importData.getUserName(), params);
    }

    /**
     * 将导入 json 存储到本地
     *
     * @param importData json
     */
    private void saveImportDataJson(DataImportDTO importData) {
        // 将数据存储到本地 log
        String importJsonPath = PathBuilders.getImportDataJsonPath(importData.getUserId(), importData.getType(), importData.getImportToken());
        String path = Files1.getPath(SystemEnvAttr.LOG_PATH.getValue(), importJsonPath);
        FileWriters.write(path, JSON.toJSONString(importData, SerializerFeature.PrettyFormat));
    }

}
