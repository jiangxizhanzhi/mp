package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.util.List;
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
     * 批量创建流表
     * @param agentInstanceIds agentInstanceIds
     */
    public void createDataCollectionFlow(List<String> agentInstanceIds) {
        String createUrl = flowUrl + "/probe/flows";
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


    /**
     * 根据agentInstanceId删除流表
     * @param agentInstanceIds agentInstanceIds
     */
    public void deleteDataCollectionFlow(List<String> agentInstanceIds) {
        String deleteUrl = flowUrl + "/probe/flows";
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
