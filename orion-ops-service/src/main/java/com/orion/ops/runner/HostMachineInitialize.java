package com.orion.ops.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orion.ops.consts.Const;
import com.orion.ops.consts.MachineEnvAttr;
import com.orion.ops.dao.MachineEnvDAO;
import com.orion.ops.dao.MachineInfoDAO;
import com.orion.ops.entity.domain.MachineEnvDO;
import com.orion.ops.entity.domain.MachineInfoDO;
import com.orion.process.Processes;
import com.orion.utils.Strings;
import com.orion.utils.Systems;
import com.orion.utils.io.Files1;
import com.orion.utils.net.IPs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 宿主机初始化
 *
 * @author ljh15
 * @version 1.0.0
 * @since 2021/3/29 18:54
 */
@Component
@Order(100)
@Slf4j
public class HostMachineInitialize implements CommandLineRunner {

    @Resource
    private MachineInfoDAO machineInfoDAO;

    @Resource
    private MachineEnvDAO machineEnvDAO;

    @Override
    public void run(String... args) {
        // 初始化宿主机
        this.initMachine();
        // 初始化环境
        this.initEnv();
    }

    /**
     * 初始化宿主机
     */
    private void initMachine() {
        LambdaQueryWrapper<MachineInfoDO> wrapper = new LambdaQueryWrapper<MachineInfoDO>()
                .eq(MachineInfoDO::getId, 1);
        MachineInfoDO machineInfo = machineInfoDAO.selectOne(wrapper);
        if (machineInfo == null) {
            MachineInfoDO insert = new MachineInfoDO();
            insert.setExtranetHost(IPs.IP);
            insert.setIntranetHost(IPs.IP);
            insert.setMachineName(Systems.HOST_NAME);
            insert.setDescription("宿主机");
            insert.setUsername(Systems.USER_NAME);
            insert.setSystemType(Systems.BE_UNIX ? 1 : 2);
            insert.setSystemVersion(Systems.OS_NAME + Strings.SPACE + Systems.OS_VERSION);
            insert.setMachineStatus(1);
            machineInfoDAO.insert(insert);
            machineInfoDAO.setId(insert.getId(), 1L);
        }
    }

    /**
     * 初始化环境
     */
    private void initEnv() {
        LambdaQueryWrapper<MachineEnvDO> wrapper = new LambdaQueryWrapper<MachineEnvDO>()
                .eq(MachineEnvDO::getMachineId, 1L);
        List<MachineEnvDO> envs = machineEnvDAO.selectList(wrapper);
        for (String key : MachineEnvAttr.getHostKeys()) {
            MachineEnvDO env = envs.stream()
                    .filter(s -> s.getAttrKey().equals(key))
                    .findFirst()
                    .orElse(null);
            if (env == null) {
                MachineEnvAttr attr = MachineEnvAttr.of(key);
                MachineEnvDO insert = new MachineEnvDO();
                insert.setMachineId(1L);
                insert.setAttrKey(key);
                insert.setAttrValue(this.getAttrValue(attr));
                insert.setDescription(attr.getDescription());
                insert.setForbidDelete(2);
                machineEnvDAO.insert(insert);
            }
        }
    }

    /**
     * 获取属性值
     *
     * @param attr attr
     * @return value
     */
    private String getAttrValue(MachineEnvAttr attr) {
        switch (attr) {
            case JAVA_BIN_PATH:
                return whereIs(attr, Const.JAVA);
            case MAVEN_BIN_PATH:
                return whereIs(attr, Const.MVN);
            case NPM_BIN_PATH:
                return whereIs(attr, Const.NPM);
            case YARN_BIN_PATH:
                return whereIs(attr, Const.YARN);
            case SVN_BIN_PATH:
                return whereIs(attr, Const.SVN);
            case GIT_BIN_PATH:
                return whereIs(attr, Const.GIT);
            case LOG_PATH:
                return createOrionOpsPath(Const.OPERATION_LOGS_PATH);
            case KEY_PATH:
                return createOrionOpsPath(Const.KEYS_PATH);
            case DIST_PATH:
                return createOrionOpsPath(Const.DIST_PATH);
            default:
                return null;
        }
    }

    /**
     * whereis 命令
     *
     * @param attr    attr
     * @param command command
     * @return path
     */
    public static String whereIs(MachineEnvAttr attr, String command) {
        try {
            String s = Processes.getOutputResultWithDirString(Const.USR_BIN, Const.WHERE_IS, command);
            String[] split = s.split(Strings.SPACE);
            if (split.length == 1) {
                return null;
            }
            return Strings.ifBlank(split[1], (String) null);
        } catch (Exception e) {
            log.error("获取系统参数: {} 失败, command: whereis {}, e: {}", attr.name(), command, e);
            return null;
        }
    }

    /**
     * 创建项目目录
     *
     * @param name name
     * @return path
     */
    public static String createOrionOpsPath(String name) {
        String dir = Systems.HOME_DIR + "/" + Const.ORION_OPS + "/" + name + "/";
        dir = Files1.getPath(dir);
        Files1.mkdirs(dir);
        return dir;
    }

}
