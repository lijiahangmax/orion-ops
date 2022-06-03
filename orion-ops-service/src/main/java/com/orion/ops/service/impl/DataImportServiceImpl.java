package com.orion.ops.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.orion.id.UUIds;
import com.orion.office.excel.reader.ExcelBeanReader;
import com.orion.ops.consts.KeyConst;
import com.orion.ops.consts.MessageConst;
import com.orion.ops.consts.app.VcsStatus;
import com.orion.ops.consts.event.EventKeys;
import com.orion.ops.consts.export.ImportType;
import com.orion.ops.consts.message.MessageType;
import com.orion.ops.consts.system.SystemEnvAttr;
import com.orion.ops.dao.*;
import com.orion.ops.entity.domain.*;
import com.orion.ops.entity.dto.importer.*;
import com.orion.ops.entity.vo.DataImportCheckRowVO;
import com.orion.ops.entity.vo.DataImportCheckVO;
import com.orion.ops.service.api.ApplicationInfoService;
import com.orion.ops.service.api.ApplicationProfileService;
import com.orion.ops.service.api.DataImportService;
import com.orion.ops.service.api.WebSideMessageService;
import com.orion.ops.utils.Currents;
import com.orion.ops.utils.PathBuilders;
import com.orion.ops.utils.Utils;
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
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
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
    private MachineProxyDAO machineProxyDAO;

    @Resource
    private FileTailListDAO fileTailListDAO;

    @Resource
    private ApplicationProfileDAO applicationProfileDAO;

    @Resource
    private ApplicationInfoDAO applicationInfoDAO;

    @Resource
    private ApplicationVcsDAO applicationVcsDAO;

    @Resource
    private CommandTemplateDAO commandTemplateDAO;

    @Resource
    private ApplicationProfileService applicationProfileService;

    @Resource
    private ApplicationInfoService applicationInfoService;

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

    // -------------------- check --------------------

    @Override
    public DataImportCheckVO checkMachineInfoImportData(List<MachineInfoImportDTO> rows) {
        // 检查数据合法性
        this.validImportRows(ImportType.MACHINE, rows);
        // 通过唯一标识查询机器
        List<MachineInfoDO> presentMachines = this.getImportRowsPresentValues(rows, MachineInfoImportDTO::getTag,
                machineInfoDAO, MachineInfoDO::getMachineTag);
        // 检查数据是否存在
        this.checkImportRowsPresent(rows, MachineInfoImportDTO::getTag,
                presentMachines, MachineInfoDO::getMachineTag, MachineInfoDO::getId);
        // 设置导入检查数据
        return this.setImportCheckRows(ImportType.MACHINE, rows);
    }

    @Override
    public DataImportCheckVO checkMachineProxyImportData(List<MachineProxyImportDTO> rows) {
        // 检查数据合法性
        this.validImportRows(ImportType.MACHINE_PROXY, rows);
        // 设置导入检查数据
        return this.setImportCheckRows(ImportType.MACHINE_PROXY, rows);
    }

    @Override
    public DataImportCheckVO checkMachineTailFileImportData(List<MachineTailFileImportDTO> rows) {
        // 检查数据合法性
        this.validImportRows(ImportType.MACHINE_TAIL_FILE, rows);
        // 设置机器id
        this.setCheckRowsRelId(rows, MachineTailFileImportDTO::getMachineTag,
                machineInfoDAO::selectIdByTagList,
                MachineInfoDO::getMachineTag,
                MachineInfoDO::getId,
                MachineTailFileImportDTO::setMachineId,
                MessageConst.UNKNOWN_MACHINE_TAG);
        // 通过别名查询文件
        List<FileTailListDO> presentFiles = this.getImportRowsPresentValues(rows, MachineTailFileImportDTO::getName,
                fileTailListDAO, FileTailListDO::getAliasName);
        // 检查数据是否存在
        this.checkImportRowsPresent(rows, MachineTailFileImportDTO::getName,
                presentFiles, FileTailListDO::getAliasName, FileTailListDO::getId);
        // 设置导入检查数据
        return this.setImportCheckRows(ImportType.MACHINE_TAIL_FILE, rows);
    }

    @Override
    public DataImportCheckVO checkAppProfileImportData(List<ApplicationProfileImportDTO> rows) {
        // 检查数据合法性
        this.validImportRows(ImportType.PROFILE, rows);
        // 通过唯一标识查询环境
        List<ApplicationProfileDO> presentProfiles = this.getImportRowsPresentValues(rows,
                ApplicationProfileImportDTO::getTag,
                applicationProfileDAO, ApplicationProfileDO::getProfileTag);
        // 检查数据是否存在
        this.checkImportRowsPresent(rows, ApplicationProfileImportDTO::getTag,
                presentProfiles, ApplicationProfileDO::getProfileTag, ApplicationProfileDO::getId);
        // 设置导入检查数据
        return this.setImportCheckRows(ImportType.PROFILE, rows);
    }

    @Override
    public DataImportCheckVO checkApplicationInfoImportData(List<ApplicationImportDTO> rows) {
        // 检查数据合法性
        this.validImportRows(ImportType.APPLICATION, rows);
        // 设置机器id
        this.setCheckRowsRelId(rows, ApplicationImportDTO::getVcsName,
                applicationVcsDAO::selectIdByNameList,
                ApplicationVcsDO::getVcsName,
                ApplicationVcsDO::getId,
                ApplicationImportDTO::setVcsId,
                MessageConst.UNKNOWN_APP_VCS);
        // 通过唯一标识查询应用
        List<ApplicationInfoDO> presentApps = this.getImportRowsPresentValues(rows,
                ApplicationImportDTO::getTag,
                applicationInfoDAO, ApplicationInfoDO::getAppTag);
        // 检查数据是否存在
        this.checkImportRowsPresent(rows, ApplicationImportDTO::getTag,
                presentApps, ApplicationInfoDO::getAppTag, ApplicationInfoDO::getId);
        // 设置导入检查数据
        return this.setImportCheckRows(ImportType.APPLICATION, rows);
    }

    @Override
    public DataImportCheckVO checkAppVcsImportData(List<ApplicationVcsImportDTO> rows) {
        // 检查数据合法性
        this.validImportRows(ImportType.VCS, rows);
        // 通过唯一标识查询应用
        List<ApplicationVcsDO> presentVcsList = this.getImportRowsPresentValues(rows,
                ApplicationVcsImportDTO::getName,
                applicationVcsDAO, ApplicationVcsDO::getVcsName);
        // 检查数据是否存在
        this.checkImportRowsPresent(rows, ApplicationVcsImportDTO::getName,
                presentVcsList, ApplicationVcsDO::getVcsName, ApplicationVcsDO::getId);
        // 设置导入检查数据
        return this.setImportCheckRows(ImportType.VCS, rows);
    }

    @Override
    public DataImportCheckVO checkCommandTemplateImportData(List<CommandTemplateImportDTO> rows) {
        // 检查数据合法性
        this.validImportRows(ImportType.COMMAND_TEMPLATE, rows);
        // 通过名称查询模板
        List<CommandTemplateDO> presentTemplates = this.getImportRowsPresentValues(rows,
                CommandTemplateImportDTO::getName,
                commandTemplateDAO, CommandTemplateDO::getTemplateName);
        // 检查数据是否存在
        this.checkImportRowsPresent(rows, CommandTemplateImportDTO::getName,
                presentTemplates, CommandTemplateDO::getTemplateName, CommandTemplateDO::getId);
        // 设置导入检查数据
        return this.setImportCheckRows(ImportType.COMMAND_TEMPLATE, rows);
    }

    // -------------------- import --------------------

    @Override
    public void importMachineInfoData(DataImportDTO importData) {
        this.doImportData(importData, machineInfoDAO, v -> {
            // ignore
        }, v -> {
            // 检查是否有代理
            Optional.ofNullable(v.getId())
                    .map(machineInfoDAO::selectById)
                    .map(MachineInfoDO::getProxyId)
                    .ifPresent(v::setProxyId);
        });
    }

    @Override
    public void importMachineProxyData(DataImportDTO importData) {
        this.doImportData(importData, machineProxyDAO);
    }

    @Override
    public void importMachineTailFileData(DataImportDTO importData) {
        this.doImportData(importData, fileTailListDAO);
    }

    @Override
    public void importAppProfileData(DataImportDTO importData) {
        // 执行导入
        this.doImportData(importData, applicationProfileDAO);
        // 删除环境缓存
        applicationProfileService.clearProfileCache();
    }

    @Override
    public void importApplicationData(DataImportDTO importData) {
        this.doImportData(importData, applicationInfoDAO, v -> {
            // 设置应用排序
            v.setAppSort(applicationInfoService.getNextSort());
        }, v -> {
            // ignore
        });
    }

    @Override
    public void importAppVcsData(DataImportDTO importData) {
        this.doImportData(importData, applicationVcsDAO, v -> {
            v.setVcsStatus(VcsStatus.UNINITIALIZED.getStatus());
        }, v -> {
            Long id = v.getId();
            ApplicationVcsDO beforeVcs = applicationVcsDAO.selectById(id);
            if (beforeVcs != null && !beforeVcs.getVscUrl().equals(v.getVscUrl())) {
                // 如果修改了url则状态改为未初始化
                v.setVcsStatus(VcsStatus.UNINITIALIZED.getStatus());
                // 删除 event 目录
                File clonePath = new File(Utils.getVcsEventDir(id));
                Files1.delete(clonePath);
            }
        });
    }

    @Override
    public void importCommandTemplateData(DataImportDTO importData) {
        this.doImportData(importData, commandTemplateDAO, v -> {
            v.setCreateUserId(importData.getUserId());
            v.setCreateUserName(importData.getUserName());
            v.setUpdateUserId(importData.getUserId());
            v.setUpdateUserName(importData.getUserName());
        }, v -> {
            v.setUpdateUserId(importData.getUserId());
            v.setUpdateUserName(importData.getUserName());
        });
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
    public void clearImportToken(String token) {
        redisTemplate.delete(Strings.format(KeyConst.DATA_IMPORT_TOKEN, Currents.getUserId(), token));
    }

    // -------------------- check private --------------------

    /**
     * 验证对象合法性
     *
     * @param importType importType
     * @param rows       rows
     */
    private void validImportRows(ImportType importType, List<? extends BaseDataImportDTO> rows) {
        for (BaseDataImportDTO row : rows) {
            try {
                importType.getValid().accept(row);
            } catch (Exception e) {
                row.setIllegalMessage(e.getMessage());
            }
        }

    }

    /**
     * 设置引用id
     *
     * @param rows               rows
     * @param symbolGetter       symbolGetter
     * @param query              dataQuery
     * @param domainSymbolGetter domainSymbolGetter
     * @param domainIdGetter     domainIdGetter
     * @param relIdSetter        relIdSetter
     * @param notPresentTemplate notPresentTemplate
     * @param <T>                row type
     * @param <S>                symbol type
     * @param <DO>               domain type
     */
    private <T extends BaseDataImportDTO, S, DO> void setCheckRowsRelId(List<T> rows,
                                                                        Function<T, S> symbolGetter,
                                                                        Function<List<S>, List<DO>> query,
                                                                        Function<DO, S> domainSymbolGetter,
                                                                        Function<DO, Long> domainIdGetter,
                                                                        BiConsumer<T, Long> relIdSetter,
                                                                        String notPresentTemplate) {
        // 获取合法数据
        List<T> validImportList = rows.stream()
                .filter(s -> Objects.isNull(s.getIllegalMessage()))
                .filter(s -> {
                    S symbol = symbolGetter.apply(s);
                    return symbol instanceof String ? Strings.isNotBlank((String) symbol) : Objects.nonNull(symbol);
                }).collect(Collectors.toList());
        if (validImportList.isEmpty()) {
            return;
        }
        // 获取标识
        List<S> symbolList = validImportList.stream()
                .map(symbolGetter)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (symbolList.isEmpty()) {
            return;
        }
        // 查询id
        Map<S, Long> symbolIdMap = Maps.newMap();
        List<DO> dataList = query.apply(symbolList);
        dataList.forEach(s -> symbolIdMap.put(domainSymbolGetter.apply(s), domainIdGetter.apply(s)));
        // 设置id
        for (T row : validImportList) {
            S symbol = symbolGetter.apply(row);
            Long relId = symbolIdMap.get(symbol);
            if (relId == null) {
                row.setIllegalMessage(Strings.format(notPresentTemplate, symbol));
                continue;
            }
            relIdSetter.accept(row, relId);
        }
    }

    /**
     * 获取导入行已存在的数据
     *
     * @param rows               rows
     * @param rowSymbolGetter    rowSymbolGetter
     * @param mapper             mapper
     * @param domainSymbolGetter domainSymbolGetter
     * @param <T>                row type
     * @param <DO>               domain type
     * @return present domain
     */
    private <T extends BaseDataImportDTO, DO> List<DO> getImportRowsPresentValues(List<T> rows,
                                                                                  Function<T, ?> rowSymbolGetter,
                                                                                  BaseMapper<DO> mapper,
                                                                                  SFunction<DO, ?> domainSymbolGetter) {
        List<?> symbolList = rows.stream()
                .filter(s -> Objects.isNull(s.getIllegalMessage()))
                .map(rowSymbolGetter)
                .collect(Collectors.toList());
        if (symbolList.isEmpty()) {
            return Lists.empty();
        } else {
            LambdaQueryWrapper<DO> wrapper = new LambdaQueryWrapper<DO>()
                    .in(domainSymbolGetter, symbolList);
            return mapper.selectList(wrapper);
        }
    }


    /**
     * 检查导入行是否存在
     *
     * @param rows            rows
     * @param rowKeyGetter    rowKeyGetter
     * @param presentValues   presentValues
     * @param presentIdGetter presentIdGetter
     * @param <R>             row type
     * @param <K>             key type
     * @param <P>             present type
     */
    private <R extends BaseDataImportDTO, K, P> void checkImportRowsPresent(List<R> rows,
                                                                            Function<R, K> rowKeyGetter,
                                                                            List<P> presentValues,
                                                                            Function<P, K> presentKeyGetter,
                                                                            Function<P, Long> presentIdGetter) {
        for (R row : rows) {
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
     * @param type type
     * @param rows rows
     * @param <R>  rowType
     * @return checkData
     */
    private <R extends BaseDataImportDTO> DataImportCheckVO setImportCheckRows(ImportType type, List<R> rows) {
        // 设置检查对象
        String dataJson = JSON.toJSONString(rows);
        List<DataImportCheckRowVO> illegalRows = Lists.newList();
        List<DataImportCheckRowVO> insertRows = Lists.newList();
        List<DataImportCheckRowVO> updateRows = Lists.newList();
        // 设置行
        for (int i = 0; i < rows.size(); i++) {
            // 设置检查数据
            R row = rows.get(i);
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
        cache.setType(type.getType());
        cache.setData(dataJson);
        cache.setCheck(check);
        redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(cache),
                KeyConst.DATA_IMPORT_TOKEN_EXPIRE, TimeUnit.SECONDS);
        return check;
    }

    // -------------------- import private --------------------

    /**
     * 执行导入数据
     *
     * @param importData 导入数据
     * @param mapper     mapper
     * @param <T>        do
     */
    public <T> void doImportData(DataImportDTO importData, BaseMapper<T> mapper) {
        this.doImportData(importData, mapper, e -> {
            // ignore
        }, e -> {
            // ignore
        });
    }

    /**
     * 执行导入数据
     *
     * @param importData   导入数据
     * @param mapper       mapper
     * @param insertFiller 插入填充器
     * @param updateFiller 修改填充器
     * @param <T>          do
     */
    @SuppressWarnings("unchecked")
    public <T> void doImportData(DataImportDTO importData, BaseMapper<T> mapper,
                                 Consumer<T> insertFiller, Consumer<T> updateFiller) {
        ImportType type = ImportType.of(importData.getType());
        Exception ex = null;
        try {
            // 获取缓存数据
            DataImportCheckVO dataCheck = importData.getCheck();
            List<BaseDataImportDTO> rows = this.getImportData(importData);
            // 插入
            this.getImportInsertData(dataCheck, rows, type.getConvertClass())
                    .stream()
                    .map(s -> (T) s)
                    .peek(insertFiller)
                    .forEach(mapper::insert);
            // 更新
            this.getImportUpdateData(dataCheck, rows, type.getConvertClass())
                    .stream()
                    .map(s -> (T) s)
                    .peek(updateFiller)
                    .forEach(mapper::updateById);
        } catch (Exception e) {
            ex = e;
            log.error("{}导入失败 token: {}, data: {}", type.name(), importData.getImportToken(), JSON.toJSONString(importData), e);
        }
        // 发送站内信
        this.sendImportWebSideMessage(importData, ex == null ? type.getSuccessType() : type.getFailureType());
        // 保存日志
        this.saveImportDataJson(importData);
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
     * 获取导入插入数据
     *
     * @param dataCheck    dataCheck
     * @param dataList     list
     * @param convertClass convertClass
     * @param <T>          T
     * @param <R>          R
     * @return rows
     */
    private <T extends BaseDataImportDTO, R> List<R> getImportInsertData(DataImportCheckVO dataCheck,
                                                                         List<T> dataList,
                                                                         Class<R> convertClass) {
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
     * @param <R>          R
     * @return rows
     */
    private <T extends BaseDataImportDTO, R> List<R> getImportUpdateData(DataImportCheckVO dataCheck,
                                                                         List<T> dataList,
                                                                         Class<R> convertClass) {
        return dataCheck.getUpdateRows().stream()
                .map(DataImportCheckRowVO::getIndex)
                .map(dataList::get)
                .map(s -> Converts.to(s, convertClass))
                .collect(Collectors.toList());
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
