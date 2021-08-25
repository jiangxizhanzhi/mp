package cn.cyberpeace.mp.datasandtable.repository;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.datasandtable.config.SituationAwareConfig;
import cn.cyberpeace.mp.datasandtable.constant.SituationalAwareErrorCode;
import cn.cyberpeace.mp.datasandtable.model.ProbeRequest;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
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
     * @param probeRequest probeRequest
     * @return String
     */
    public String addScript(ProbeRequest probeRequest) {
        var sentUrl = situationAwareConfig.getWazuhManagerUrl() + "/probe";
        var jsonObject = new JSONObject();
        jsonObject.put("ScriptId", probeRequest.getProbeId());
        jsonObject.put("Content", probeRequest.getScriptContent());
        jsonObject.put("ProbeType", probeRequest.getProbeCategory());
        jsonObject.put("ProbeName", probeRequest.getProbeName());
        jsonObject.put("OSType", probeRequest.getOsType());
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
     * @param probeId
     * @return
     */
    public String deleteProbeByProbeId(String probeId) {
        var sentUrl = situationAwareConfig.getWazuhManagerUrl()+"/" + probeId;
        try {
            return HttpUtils.sendDelete(sentUrl);
        } catch (IOException e) {
            log.error(SituationalAwareErrorCode.PROBE_MANAGER_ADD_ERROR.getMessage(), ExceptionUtils.getStackTrace(e));
            throw new MpRuntimeException(SituationalAwareErrorCode.PROBE_MANAGER_ADD_ERROR);
        }
    }


    //todo windows 的场景没有覆盖
    private String buildExecCommandline(ProbeRequest probeRequest) {
        switch (probeRequest.getScriptType()){
            case 0:
                return "sh " + situationAwareConfig.getWazuhManagerUrl() +probeRequest.getProbeName() + ".sh";
            case 1:
                return "python " + probeRequest.getProbeName() + ".py";
            default:
                break;
        }
        return null;
    }
}
