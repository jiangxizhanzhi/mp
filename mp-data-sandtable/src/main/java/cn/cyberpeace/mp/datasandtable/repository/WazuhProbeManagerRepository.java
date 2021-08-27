package cn.cyberpeace.mp.datasandtable.repository;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.datasandtable.config.SituationAwareConfig;
import cn.cyberpeace.mp.datasandtable.constant.SituationalAwareErrorCode;
import cn.cyberpeace.mp.datasandtable.model.ProbeRequest;
import cn.cyberpeace.mp.datasandtable.model.enums.OsType;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static cn.cyberpeace.mp.datasandtable.model.enums.ScriptType.valueOf;

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
    public String addScript(ProbeRequest probeRequest) {
        var sentUrl = situationAwareConfig.getWazuhManagerUrl() + "/probe";
        var jsonObject = new JSONObject();
        jsonObject.put("ScriptId", probeRequest.getProbeId());
        jsonObject.put("Content", probeRequest.getScriptContent());
        jsonObject.put("ProbeType", probeRequest.getScriptType().getTypeVal());
        jsonObject.put("ProbeName", probeRequest.getProbeName());
        jsonObject.put("OSType", probeRequest.getOsType().getTypeVal());
        jsonObject.put("Interval", probeRequest.getCommandInterval());
        jsonObject.put("Commandline", buildExecCommandline(probeRequest));
        try {
            return HttpUtils.sendPost(sentUrl, jsonObject.toJSONString());
        } catch (IOException e) {
            log.error(SituationalAwareErrorCode.PROBE_MANAGER_ADD_ERROR.getMessage(), ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(SituationalAwareErrorCode.PROBE_MANAGER_ADD_ERROR);
        }
    }

    /**
     * 删除脚本
     *
     * @param probeId
     * @return
     */
    public String deleteProbeByProbeId(String probeId) {
        var sentUrl = situationAwareConfig.getWazuhManagerUrl() + "/probe/" + probeId;
        try {
            String deleteResult = HttpUtils.sendDelete(sentUrl);
            log.info("delete probe id: {} {}:", probeId, deleteResult);
            return deleteResult;
        } catch (IOException e) {
            log.error(SituationalAwareErrorCode.PROBE_MANAGER_ADD_ERROR.getMessage(), ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(SituationalAwareErrorCode.PROBE_MANAGER_ADD_ERROR);
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
        return commandLineBuilder.append(buildExecString(probeRequest)).append(" ").append(buildScriptPath(probeRequest))
            .append("/").append(buildProbeFileName(probeRequest)).append(buildProbeFileSubffix(probeRequest)).toString();
    }

    private String buildExecString(ProbeRequest probeRequest) {
        switch (probeRequest.getScriptType()) {
            case SHELL:
                return "sh";
            case PYTHON:
                return "python";
            case BAT:
                return "call";
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
        switch (probeRequest.getOsType()) {
            case LINUX:
                osType = "linux";
                break;
            case WINDOWS:
                osType = "windows";
                break;
            default:
                osType = "linux";
                break;
        }

        return probeRequest.getProbeName() + "_" + osType;
    }

    private String buildProbeFileSubffix(ProbeRequest probeRequest) {
        String subbfix;
        switch (probeRequest.getScriptType()) {
            case SHELL:
                subbfix = ".sh";
                break;
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
