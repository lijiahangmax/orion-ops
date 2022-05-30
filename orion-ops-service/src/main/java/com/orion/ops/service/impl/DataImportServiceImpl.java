package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.id.UUIds;
import com.orion.office.excel.reader.ExcelBeanReader;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.event.EventKeys;
import com.orion.ops.consts.export.ImportType;
import com.orion.ops.consts.message.MessageType;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.ops.entity.dto.importer.BaseDataImportDTO;
import com.orion.ops.entity.dto.importer.DataImportDTO;
import com.orion.ops.entity.dto.importer.MachineInfoImportDTO;
import com.orion.ops.entity.vo.DataImportCheckRowVO;
import com.orion.ops.entity.vo.DataImportCheckVO;
import com.orion.ops.service.api.DataImportService;
import com.orion.ops.service.api.WebSideMessageService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.PathBuilders;
import com.orion.utils.Exceptions;
import com.orion.utils.Strings;
import com.orion.utils.Valid;
import com.orion.utils.collect.Lists;
import com.orion.utils.collect.Maps;
import com.orion.utils.convert.Converts;
import com.orion.utils.io.FileWriters;
import com.orion.utils.io.Files1;
import com.orion.utils.time.Dates;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    private WebSideMessageService webSideMessageService;

    @Resource
    private MachineInfoDAO machineInfoDAO;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> parseImportWorkbook(ImportType type, Workbook workbook) {
        ExcelBeanReader<T> reader = new ExcelBeanReader<T>(workbook, workbook.getSheetAt(0), (Class<T>) type.getImportClass());
        return reader.skip(2)
                .read()
                .getRows();
    }

    @Override
    public DataImportCheckVO checkMachineImportData(List<MachineInfoImportDTO> rows) {
        // 检查数据合法性
        this.validImportData(ImportType.MACHINE, rows);
        // 设置检查对象
        String dataJson = JSON.toJSONString(rows);
        List<DataImportCheckRowVO> illegalRows = Lists.newList();
        List<DataImportCheckRowVO> insertRows = Lists.newList();
        List<DataImportCheckRowVO> updateRows = Lists.newList();
        // 通过 tag 查询机器
        List<String> tagList = rows.stream()
                .filter(s -> Objects.isNull(s.getIllegalMessage()))
                .map(MachineInfoImportDTO::getTag)
                .collect(Collectors.toList());
        LambdaQueryWrapper<MachineInfoDO> wrapper = new LambdaQueryWrapper<MachineInfoDO>()
                .in(MachineInfoDO::getMachineTag, tagList);
        List<MachineInfoDO> machines = machineInfoDAO.selectList(wrapper);
        // 设置数据
        for (int i = 0; i < rows.size(); i++) {
            MachineInfoImportDTO row = rows.get(i);
            // 设置检查数据
            DataImportCheckRowVO checkRow = Converts.to(row, DataImportCheckRowVO.class);
            checkRow.setIndex(i);
            checkRow.setRow(i + 3);
            // 不合法数据
            if (checkRow.getIllegalMessage() != null) {
                illegalRows.add(checkRow);
                continue;
            }
            final boolean update = machines.stream().anyMatch(m -> m.getMachineTag().equals(row.getTag()));
            if (update) {
                updateRows.add(checkRow);
            } else {
                insertRows.add(checkRow);
            }
        }
        // 返回数据
        return this.setCheckData(ImportType.MACHINE.getType(), dataJson, illegalRows, insertRows, updateRows);
    }

    @Override
    public DataImportDTO checkImportToken(String token) {
        // 查询缓存
        String data = redisTemplate.opsForValue().get(Strings.format(KeyConst.DATA_IMPORT_TOKEN, Currents.getUserId(), token));
        if (Strings.isEmpty(data)) {
            throw Exceptions.argument(MessageConst.OPERATOR_TIMEOUT);
        }
        return JSON.parseObject(data, DataImportDTO.class);
    }

    @Override
    public void importMachineData(DataImportDTO importData) {
        Exception ex = null;
        try {
            // 获取缓存数据
            List<MachineInfoImportDTO> rows = this.getImportData(importData);
            DataImportCheckVO dataCheck = importData.getCheck();
            // 插入
            List<DataImportCheckRowVO> insertRows = dataCheck.getInsertRows();
            for (DataImportCheckRowVO insertRow : insertRows) {
                // 获取需要插入的数据
                MachineInfoImportDTO insertImportRow = rows.get(insertRow.getIndex());
                MachineInfoDO insert = Converts.to(insertImportRow, MachineInfoDO.class);
                machineInfoDAO.insert(insert);
            }
            // 更新
            List<DataImportCheckRowVO> updateRows = dataCheck.getUpdateRows();
            for (DataImportCheckRowVO updateRow : updateRows) {
                // 获取需要更新的数据
                MachineInfoImportDTO updateImportRow = rows.get(updateRow.getIndex());
                MachineInfoDO update = Converts.to(updateImportRow, MachineInfoDO.class);
                LambdaQueryWrapper<MachineInfoDO> wrapper = new LambdaQueryWrapper<MachineInfoDO>()
                        .eq(MachineInfoDO::getMachineTag, update.getMachineTag());
                machineInfoDAO.update(update, wrapper);
            }
        } catch (Exception e) {
            ex = e;
            log.error("机器信息导入失败 token: {}, data: {}", importData.getImportToken(), JSON.toJSONString(importData), e);
        }
        // 发送站内信
        this.sendImportWebSideMessage(importData, ex == null ? MessageType.MACHINE_IMPORT_SUCCESS : MessageType.MACHINE_IMPORT_FAILURE);
        // 保存日志
        this.saveImportDataJson(importData);
    }

    @Override
    public void clearImportToken(String token) {
        redisTemplate.delete(Strings.format(KeyConst.DATA_IMPORT_TOKEN, Currents.getUserId(), token));
    }

    /**
     * 验证对象合法性
     *
     * @param importType importType
     * @param rows       rows
     */
    private void validImportData(ImportType importType, List<? extends BaseDataImportDTO> rows) {
        for (BaseDataImportDTO row : rows) {
            try {
                importType.getValid().accept(row);
            } catch (Exception e) {
                row.setIllegalMessage(e.getMessage());
            }
        }

    }

    /**
     * 设置检查数据
     *
     * @param type        type
     * @param dataJson    json
     * @param illegalRows 无效数据
     * @param insertRows  插入数据
     * @param updateRows  更新数据
     * @return 检查数据
     */
    private DataImportCheckVO setCheckData(Integer type,
                                           String dataJson,
                                           List<DataImportCheckRowVO> illegalRows,
                                           List<DataImportCheckRowVO> insertRows,
                                           List<DataImportCheckRowVO> updateRows) {
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
        cache.setType(type);
        cache.setData(dataJson);
        cache.setCheck(check);
        redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(cache),
                KeyConst.DATA_IMPORT_TOKEN_EXPIRE, TimeUnit.SECONDS);
        return check;
    }

    /**
     * 获取导入数据
     *
     * @param data data
     * @param <T>  T
     * @return list
     */
    @SuppressWarnings("unchecked")
    private <T extends BaseDataImportDTO> List<T> getImportData(DataImportDTO data) {
        ImportType type = ImportType.of(data.getType());
        Valid.notNull(type);
        return (List<T>) JSON.parseArray(data.getData(), type.getImportClass());
    }

    /**
     * 发送站内信
     *
     * @param importData importData
     * @param type       type
     */
    private void sendImportWebSideMessage(DataImportDTO importData, MessageType type) {
        // 站内信参数
        Map<String, Object> params = Maps.newMap();
        params.put(EventKeys.TIME, Dates.format(importData.getImportTime()));
        params.put(EventKeys.TOKEN, importData.getImportToken());
        webSideMessageService.addMessage(type, importData.getUserId(), importData.getUserName(), params);
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
