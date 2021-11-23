package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
  *@Desciption 数据采集流表管理
  *@Author zhangjin
  *@Date 11/22/21 10:54 上午
**/
@Service
@Slf4j
public class DataCollectionFlowTableService{

    @Value("${mp.situationaware.wazuh.flow-table-url}")
    private String flowUrl;

    /**
     * wazuh agent 创建流表
     * @param agentInstanceId
     */
    public void createDataCollectionFlow(String agentInstanceId) {
        String createUrl = flowUrl + "/probe/flows";
        var param = new JSONObject();
        param.put("instance_id", agentInstanceId);
        try {
            HttpUtils.sendPost(createUrl, param.toJSONString());
        } catch (IOException e) {
            log.info("create flow table error ,instance id {}", agentInstanceId);
            throw new MpRuntimeException(SituationalAwareErrorCode.ADD_FLOWTABLE_ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 根据agentInstanceId删除流表
     * @param agentInstanceId agentInstanceId
     */
    public void deleteDataCollectionFlow(String agentInstanceId) {
        String deleteUrl = flowUrl + "/probe/flows" + "/" + agentInstanceId;
        try {
            HttpUtils.sendDelete(deleteUrl);
        } catch (IOException e) {
            log.info("delete flow table error ,instance id {}", agentInstanceId);
            throw new MpRuntimeException(SituationalAwareErrorCode.DELETE_FLOWTABLE_ERROR, ExceptionUtils.getStackTrace(e));
        }
    }
}
