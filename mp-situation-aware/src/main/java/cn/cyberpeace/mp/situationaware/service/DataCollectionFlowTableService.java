package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


@Service
@Slf4j
public class DataCollectionFlowTableService{

    @Autowired
    private SituationAwareConfig situationAwareConfig;


    
    public void createDataCollectionFlow(List<String> agentInstanceIds) {
        String createUrl = situationAwareConfig.getWazuhFlowTableUrl() + "/probe/flows";
        var param = new JSONObject();
        param.put("instance_ids", agentInstanceIds);
        try {
            log.info("create data collection flow request params:{}", param.toJSONString());
            String result = HttpUtils.sendPost(createUrl, param.toJSONString());
            log.info("create data collection flow result:{}", result);
        } catch (IOException e) {
            log.info("create flow table error ,instance id {}", agentInstanceIds);
            throw new MpRuntimeException(SituationalAwareErrorCode.ADD_FLOWTABLE_ERROR, ExceptionUtils.getStackTrace(e));
        }
    }


    
    public void deleteDataCollectionFlow(List<String> agentInstanceIds) {
        String deleteUrl = situationAwareConfig.getWazuhFlowTableUrl() + "/probe/flows";
        var param = new JSONObject();
        param.put("instance_ids", agentInstanceIds);

        try {
            HttpUtils.sendDelete(deleteUrl, param.toJSONString());
        } catch (IOException e) {
            log.info("delete flow table error ,instance id {}", agentInstanceIds);
            throw new MpRuntimeException(SituationalAwareErrorCode.DELETE_FLOWTABLE_ERROR, ExceptionUtils.getStackTrace(e));
        }
    }
}
