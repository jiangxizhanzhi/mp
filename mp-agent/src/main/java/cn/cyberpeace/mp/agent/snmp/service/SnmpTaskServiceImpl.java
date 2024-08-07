package cn.cyberpeace.mp.agent.snmp.service;

import cn.cyberpeace.mp.agent.snmp.model.Command;
import cn.cyberpeace.mp.agent.snmp.model.CommandConfig;
import cn.cyberpeace.mp.agent.snmp.model.Param;
import cn.cyberpeace.mp.agent.snmp.model.SnmpAccessInfo;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.cyberpeace.mp.base.constant.BaseErrorCode.CUSTOM_ERROR;

@Slf4j
@Service("snmpTaskService")
public class SnmpTaskServiceImpl {

    public String getSnmpData(SnmpAccessInfo accessInfo, String oid) {
        SnmpAccessUtil snmpAccessUtil;

        try {
            snmpAccessUtil = SnmpAccessUtil.initSnmpAccessUtilFromTaskRequest(accessInfo);
        } catch (Exception e) {
            log.error("snmp连接失败", e);
            return null;
        }

        var execResult = snmpAccessUtil.exec(SnmpAccessUtil.GET, oid);
        log.info("Snmp请求成功，请求信息[{}]，返回信息{}", JsonCommon.stringify(accessInfo), JsonCommon.stringify(execResult));
        return "";
    }

    public Boolean execTask(SnmpAccessInfo accessInfo, CommandConfig config) {
        SnmpAccessUtil snmpAccessUtil;

        // 1. connect
        try {
            snmpAccessUtil = SnmpAccessUtil.initSnmpAccessUtilFromTaskRequest(accessInfo);
        } catch (Exception e) {
            log.error("snmp连接失败", e);
            return false;
        }

        // 2. exec command
        List<String> inputAndOutput = new ArrayList<>();

        try {
            List<Command> commands = config.getCommands();
            for (Command command : commands) {
                List<JSONObject> execResult = new ArrayList<>();
                for (String finalCommand : formatConfig(command).lines().collect(Collectors.toList())) {
                    inputAndOutput.add(finalCommand);

                    String[] formatSnmpCommand = finalCommand.split(" ");
                    String method = formatSnmpCommand[0].toUpperCase();
                    String oid = formatSnmpCommand[1];

                    if (SnmpAccessUtil.SET.equals(method)) {
                        String value = formatSnmpCommand[2];
                        execResult = snmpAccessUtil.exec(SnmpAccessUtil.SET, oid, value);
                    } else if (SnmpAccessUtil.GET.equals(method) || SnmpAccessUtil.GET_BULK.equals(method) || SnmpAccessUtil.GET_NEXT.equals(method) || SnmpAccessUtil.WALK.equals(method)) {
                        execResult = snmpAccessUtil.exec(method, oid);
                    } else {
                        throw new MpRuntimeException(CUSTOM_ERROR.format("命令格式异常，snmp命令格式为 (set oid value) | ([get|get_bulk|walk|get_next] oid)"));
                    }

                    inputAndOutput.add(JsonUtils.stringify(execResult));
                    log.info("Snmp请求成功，请求信息[{}]，返回信息{}", finalCommand, JsonUtils.stringify(execResult));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        } finally {
            if (snmpAccessUtil != null) {
                snmpAccessUtil.close();
            }
        }

        return Boolean.TRUE;
    }

    private String formatConfig(Command command) {
        String finalCommand = command.getCommand();

        for (Param param : command.getParams()) {

            finalCommand = finalCommand.replaceAll("\\$\\{" + param.getCode() + "}", param.getValue());
        }

        return finalCommand;
    }
}

