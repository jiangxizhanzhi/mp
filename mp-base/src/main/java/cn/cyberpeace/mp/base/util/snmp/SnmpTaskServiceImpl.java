package cn.cyberpeace.mp.base.util.snmp;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.snmp_device.juniper.JunosSrxFirewall;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

import static cn.cyberpeace.mp.base.constant.BaseErrorCode.CUSTOM_ERROR;

@Slf4j
public class SnmpTaskServiceImpl {

    public static List<String> walkSnmpData(SnmpAccessInfo accessInfo, String oid) {
        SnmpAccessUtil snmpAccessUtil = null;

        try {
            log.info("尝试建立snmp链接, 参数为 {} , oid为 {}", accessInfo, oid);
            snmpAccessUtil = SnmpAccessUtil.initSnmpAccessUtilFromTaskRequest(accessInfo);
            List<String> value = snmpAccessUtil.exec(SnmpAccessUtil.WALK, oid).stream().map(obj -> obj.get("value").toString()).collect(Collectors.toList());
            return value;
        } catch (Exception e) {
            log.error("snmp连接失败", e);
            return Collections.emptyList();
        } finally {
            if (snmpAccessUtil!= null) {
                snmpAccessUtil.close();
            }
        }

    }

    public static String getSnmpData(SnmpAccessInfo accessInfo, String oid) {
        SnmpAccessUtil snmpAccessUtil = null;

        try {
            log.info("尝试建立snmp链接, 参数为 {} , oid为 {}", accessInfo, oid);
            snmpAccessUtil = SnmpAccessUtil.initSnmpAccessUtilFromTaskRequest(accessInfo);
            String value = snmpAccessUtil.exec(SnmpAccessUtil.GET, oid).get(0).get("value").toString();

            return value;
        } catch (Exception e) {
            log.error("snmp连接失败", e);
            return null;
        } finally {
            if (snmpAccessUtil!= null) {
                snmpAccessUtil.close();
            }
        }

    }

    public static Map<String,String> getSnmpData(SnmpAccessInfo accessInfo, List<String> oids) {
        SnmpAccessUtil snmpAccessUtil;

        try {
            log.info("尝试建立snmp链接, 参数为 {} , oid为 {}", accessInfo, oids);
            snmpAccessUtil = SnmpAccessUtil.initSnmpAccessUtilFromTaskRequest(accessInfo);
        } catch (Exception e) {
            log.error("snmp连接失败", e);
            return null;
        }

        Map<String, String> result = new HashMap<>();
        for (String oid : oids) {
            var execResult = snmpAccessUtil.exec(SnmpAccessUtil.GET, oid);
            // 返回结构类似,OID均为大写字母  [{"OID":"1.3.6.1.4.1.2021.4.11.0","value":"118268"}]
            Object value = execResult.get(0).get("value");
            result.put(oid, String.valueOf(value));
        }
        log.info("查询结果汇总为:{}", result);
        return result;
    }

    public Map<String,String> walkSnmpData(SnmpAccessInfo accessInfo, List<String> oids) {
        SnmpAccessUtil snmpAccessUtil;

        try {
            log.info("尝试建立snmp链接, 参数为 {} , oid为 {}", JsonCommon.stringify(accessInfo), JsonCommon.stringify(oids));
            snmpAccessUtil = SnmpAccessUtil.initSnmpAccessUtilFromTaskRequest(accessInfo);
        } catch (Exception e) {
            log.error("snmp连接失败", e);
            return null;
        }

        Map<String,String> result = new HashMap<>();
        for (String oid : oids) {
            var execResult = snmpAccessUtil.exec(SnmpAccessUtil.WALK, oid);
            for (var r : execResult) {
                result.put(r.getString("OID"), String.valueOf(r.get("value")));
            }
        }
        log.info("查询结果汇总为:{}",JsonCommon.stringify(result));
        return result;
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

    @Getter
    @Setter
    public static class SnmpResult {
        private String oID;
        private Object value;
    }


    public static void main(String[] args) {
        JunosSrxFirewall junosSrxFirewall = new JunosSrxFirewall("10.2.99.103");
        log.info("image name:{}, system info：{}， CPU usage：{}， memory usage：{}， flash usage：{}", junosSrxFirewall.getImageName(),
                junosSrxFirewall.getSystemDesc(), junosSrxFirewall.getCpuUsage(), junosSrxFirewall.memoryUsage(), junosSrxFirewall.diskUsage());
        log.info("info");
    }
}


