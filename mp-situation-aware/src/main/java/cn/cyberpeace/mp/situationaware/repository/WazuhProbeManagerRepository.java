package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import cn.cyberpeace.mp.situationaware.model.ProbeRequest;
import cn.cyberpeace.mp.situationaware.model.enums.OsType;
import cn.cyberpeace.mp.situationaware.model.enums.ScriptType;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class WazuhProbeManagerRepository {

    @Autowired
    private SituationAwareConfig situationAwareConfig;

    /**
     * 新增脚本
     *
     * @param probeRequest probeRequest
     * @return String
     */
    public void addProbeTemplate(ProbeRequest probeRequest) {
        var sentUrl = situationAwareConfig.getWazuhManagerUrl() + "/probe";
        var jsonObject = new JSONObject();
        jsonObject.put("ScriptId", probeRequest.getProbeId());
        jsonObject.put("Content", probeRequest.getScriptContent());
        jsonObject.put("ProbeType", probeRequest.getScriptType().getTypeVal());
        jsonObject.put("ProbeName", probeRequest.getProbeName());
        jsonObject.put("OSType", probeRequest.getOsType().getTypeVal());
        jsonObject.put("Interval", probeRequest.getCommandInterval() + "m");
        jsonObject.put("Commandline", buildExecCommandline(probeRequest));
        try {
            var res = HttpUtils.sendPost(sentUrl, jsonObject.toJSONString());
            log.info("probe manager add script {}", res);
        } catch (Exception e) {
            log.error(SituationalAwareErrorCode.PROBE_MANAGER_ADD_ERROR.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 删除脚本
     *
     * @param probeId
     * @return
     */
    public void deleteProbeTemplateByProbeId(String probeId) {
        var sentUrl = situationAwareConfig.getWazuhManagerUrl() + "/probe/" + probeId;
        try {
            String deleteResult = HttpUtils.sendDelete(sentUrl);
            log.info("delete probe id: {} {}:", probeId, deleteResult);
        } catch (Exception e) {
            log.error(SituationalAwareErrorCode.PROBE_MANAGER_ADD_ERROR.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 绑定场景内的实例到探针模板
     * @param agentNames agentNames
     * @param probeId probeId
     * @return
     */
    public void bindAgentsWithProbeTemplate(List<String> agentNames, String probeId) {
        var sentUrl = situationAwareConfig.getWazuhManagerUrl() + "/probe/probes/bind_agent";
        var jsonObject = new JSONObject();
        jsonObject.put("ScriptId", probeId);
        jsonObject.put("AgentNames", agentNames);
        try {
            String res =  HttpUtils.sendPost(sentUrl, jsonObject.toJSONString());
            log.info("bind probe res{}:", res);
        } catch (Exception e) {
            log.error(SituationalAwareErrorCode.PROBE_MANAGER_BIND_ERROR.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 解绑定场景内的实例到探针模板
     * @param agentNames agentNames
     * @param probeId probeId
     * @return
     */
    public void unbindAgentsWithProbeTemplate(List<String> agentNames, String probeId) {
        var sentUrl = situationAwareConfig.getWazuhManagerUrl() + "/probe/probes/bind_agent";
        var jsonObject = new JSONObject();
        jsonObject.put("ScriptId", probeId);
        jsonObject.put("AgentNames", agentNames);
        try {
            String res =  HttpUtils.sendDelete(sentUrl, jsonObject.toJSONString());
            log.info("unbind probe res {}:", res);
        } catch (Exception e) {
            log.error(SituationalAwareErrorCode.PROBE_MANAGER_BIND_ERROR.getMessage(), ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 构建命令行执行路径
     *
     * @param probeRequest probeRequest
     * @return 路径地址
     */
    private String buildExecCommandline(ProbeRequest probeRequest) {
        var commandLineBuilder = new StringBuilder();
        return commandLineBuilder.append(buildExecString(probeRequest))
            .append(probeRequest.getScriptType().equals(ScriptType.BAT) ? "" : " ").append(buildScriptPath(probeRequest))
            .append(buildProbeFileName(probeRequest)).append(buildProbeFileSubffix(probeRequest)).toString();
    }

    private String buildExecString(ProbeRequest probeRequest) {
        switch (probeRequest.getScriptType()) {
            case PYTHON:
                return "python";
            case BAT:
                return "";
            default:
                return "sh";
        }
    }

    private String buildScriptPath(ProbeRequest probeRequest) {
        if (OsType.LINUX.equals(probeRequest.getOsType())) {
            return situationAwareConfig.getScriptLinuxPath();
        } else {
            return situationAwareConfig.getScripWindowsPath();
        }
    }

    private String buildProbeFileName(ProbeRequest probeRequest) {
        String osType;
        if (probeRequest.getOsType() == OsType.WINDOWS) {
            osType = "windows";
        } else {
            osType = "linux";
        }

        return probeRequest.getProbeName() + "_" + osType;
    }

    private String buildProbeFileSubffix(ProbeRequest probeRequest) {
        String subbfix;
        switch (probeRequest.getScriptType()) {
            case PYTHON:
                subbfix = ".py";
                break;
            case BAT:
                subbfix = ".bat";
                break;
            default:
                subbfix = ".sh";
                break;
        }

        return subbfix;
    }
}
