package cn.cyberpeace.mp.collection.util;

import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.collection.core.model.FileEvent;
import cn.cyberpeace.mp.collection.core.model.WazuhFileRoot;
import cn.cyberpeace.mp.collection.core.model.WazuhHostState;
import cn.cyberpeace.mp.collection.core.model.WazuhSystemUser;
import cn.cyberpeace.mp.collection.core.model.enums.FileEventType;
import cn.cyberpeace.mp.collection.core.model.sic.HostStateOutput;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConvertModelUtil {
    private ConvertModelUtil() {

    }

    /**
     * 采集事件转换
     * @param wazuhFileRoot
     * @return
     */
    public static FileEvent getFileEvent(WazuhFileRoot wazuhFileRoot) {
        FileEvent.FileEventBuilder builder = FileEvent.builder();
        if (Objects.nonNull(wazuhFileRoot.getSyscheck())) {
            builder.filePath(wazuhFileRoot.getSyscheck().getPath())
                    .size(wazuhFileRoot.getSyscheck().getSizeAfter())
                    .user(wazuhFileRoot.getSyscheck().getUnameAfter())
                    .action(wazuhFileRoot.getSyscheck().getEvent())
                    .md5Before(wazuhFileRoot.getSyscheck().getMd5Before())
                    .md5After(wazuhFileRoot.getSyscheck().getMd5After());
        }

        if (Objects.nonNull(wazuhFileRoot.getData()) && Objects.nonNull(wazuhFileRoot.getData().getWin()) &&
                Objects.nonNull(wazuhFileRoot.getData().getWin().getSystem())) {
            builder.message(wazuhFileRoot.getData().getWin().getSystem().getMessage());
        }

        if (Objects.nonNull(wazuhFileRoot.getData()) && Objects.nonNull(wazuhFileRoot.getData().getSca())) {
            builder.message(wazuhFileRoot.getData().getSca().getDescription());
            if (Objects.nonNull(wazuhFileRoot.getData().getSca().getCheck())) {
                builder.message(wazuhFileRoot.getData().getSca().getCheck().getDescription());
            }
        }
        builder.agentName(wazuhFileRoot.getAgent().getName())
                .agentId(wazuhFileRoot.getAgent().getId())
                .agentIp(wazuhFileRoot.getAgent().getIp());

        if (Objects.nonNull(wazuhFileRoot.getRule())) {
            //区分文件还是注册表
            builder.description(wazuhFileRoot.getRule().getDescription())
                    .level(wazuhFileRoot.getRule().getLevel())
                    .ruleId(wazuhFileRoot.getRule().getId());
            if (CollectionUtils.isNotEmpty(wazuhFileRoot.getRule().getGroups())) {
                builder.fileEventType(wazuhFileRoot.getRule().getGroups().stream().anyMatch("syscheck_registry"::equals) ? FileEventType.REGISTRY : FileEventType.FILE);
            }
        }
        builder.content(processContent(wazuhFileRoot));

        return builder.updateTime(TimeUtils.dateToLocalDateTime(wazuhFileRoot.getTimestamp())).build();
    }

    /**
     * 主机状态信息转换
     * @param output
     * @return
     */
    public static WazuhHostState convertHostStateOutput2WazuhHost(HostStateOutput output) {
        WazuhHostState wazuhHostState = new WazuhHostState();
        if (StringUtils.isNotBlank(output.getDiskTotal())) {
            wazuhHostState.setDiskTotal(string2Long(output.getDiskTotal()));
        }

        if (StringUtils.isNotBlank(output.getDiskUsed())) {
            wazuhHostState.setDiskUsed(string2Long(output.getDiskUsed()));
        }

        if (StringUtils.isNotBlank(output.getDiskTotal()) && StringUtils.isNotBlank(output.getDiskUsed())) {
            wazuhHostState.setDiskUnused(wazuhHostState.getDiskTotal() - wazuhHostState.getDiskUsed());
        }

        if (wazuhHostState.getDiskTotal() > 0 && wazuhHostState.getDiskUsed() > 0) {
            double used = BigDecimal.valueOf(wazuhHostState.getDiskUsed()).doubleValue();
            double total = BigDecimal.valueOf(wazuhHostState.getDiskTotal()).doubleValue();
            // 换算百分比 要 * 100
            wazuhHostState.setDiskPercent(NumberUtil.round(used * 100 / total, 2).doubleValue());
        }

        if (StringUtils.isNotBlank(output.getCPUCount())) {
            wazuhHostState.setCpuCount(Integer.parseInt(output.getCPUCount()));
        }

        if (StringUtils.isNotBlank(output.getCPUFreq())) {
            wazuhHostState.setCpuFreq(Double.parseDouble(output.getCPUFreq()));
        }

        if (StringUtils.isNotBlank(output.getCPUPercent())) {
            wazuhHostState.setCpuTotalPercent(Double.parseDouble(output.getCPUPercent()));
        }

        if (StringUtils.isNotBlank(output.getMemoryFree())) {
            wazuhHostState.setMemoryFree(string2Long(output.getMemoryFree()));
        }

        if (StringUtils.isNotBlank(output.getMemoryTotal())) {
            wazuhHostState.setMemoryTotal(string2Long(output.getMemoryTotal()));
        }

        if (StringUtils.isNotBlank(output.getMemoryUsedPercent())) {
            wazuhHostState.setMemoryPercent(Double.parseDouble(output.getMemoryUsedPercent()));
        }

        if (StringUtils.isBlank(output.getDownloadSpeed())) {
            wazuhHostState.setDownloadSpeed("0.00KB/s");
        } else {
            wazuhHostState.setDownloadSpeed(output.getDownloadSpeed());
        }

        if (StringUtils.isBlank(output.getUploadSpeed())) {
            wazuhHostState.setUploadSpeed("0.00KB/s");
        } else {
            wazuhHostState.setUploadSpeed(output.getUploadSpeed());
        }

        if (StringUtils.isBlank(output.getDiskReadBytes())) {
            wazuhHostState.setDiskReadBytes(0);
        } else {
            wazuhHostState.setDiskReadBytes(Long.parseLong(output.getDiskReadBytes()));
        }

        if (StringUtils.isBlank(output.getDiskWriteBytes())) {
            wazuhHostState.setDiskWriteBytes(0);
        } else {
            wazuhHostState.setDiskWriteBytes(Long.parseLong(output.getDiskWriteBytes()));
        }

        return wazuhHostState;
    }

    /**
     * 转换主机系统用户信息
     * @param jsonArray
     * @param jsonObject
     * @param acton
     * @return
     */
    public static List<WazuhSystemUser> processJSONArray(JSONArray jsonArray, JSONObject jsonObject) {
        List<WazuhSystemUser> list = new ArrayList<>();
        if (Objects.isNull(jsonArray)) {
            return list;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject data = jsonArray.getJSONObject(i);
            WazuhSystemUser systemUser = JSON.toJavaObject(data, WazuhSystemUser.class);
            if (Objects.nonNull(jsonObject.get("time"))) {
                systemUser.setTime((LocalDateTime) jsonObject.get("time"));
            }
            systemUser.setAgentIp((String) jsonObject.get("agent_ip"));
            systemUser.setAgentName((String) jsonObject.get("agent_name"));
            systemUser.setDescription("USER MODIFIED");
            list.add(systemUser);
        }
        return list;
    }

    private static Long string2Long(String val) {
        double doubleVal = Double.parseDouble(val);
        return BigDecimal.valueOf(doubleVal).longValue();
    }

    private static JSONObject processContent(WazuhFileRoot wazuhFileRoot) {
        JSONObject jsonObject = new JSONObject();
        WazuhFileRoot.Rule rule = wazuhFileRoot.getRule();
        if (Objects.nonNull(rule)) {
            JSONObject ruleObj = new JSONObject();
            ruleObj.put("firedtimes", rule.getFiredtimes());
            ruleObj.put("description", rule.getDescription());
            ruleObj.put("groups", rule.getGroups());
            ruleObj.put("id", rule.getId());
            jsonObject.put("rule", ruleObj);
        }
        jsonObject.put("full_log", wazuhFileRoot.getFullLog());
        return jsonObject;
    }
}
