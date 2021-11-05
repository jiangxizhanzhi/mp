package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.util.HttpUtils;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
import cn.cyberpeace.mp.flow.repository.FlowActuatorMapper;
import cn.cyberpeace.mp.flow.service.FlowActuatorService;
import cn.cyberpeace.mp.flow.service.field.FlowActuatorFields;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Description: 流量执行器service
 * @Author qiaokang
 * @Date：2021/6/10 10:31
 */
@Service
@Slf4j
public class FlowActuatorServiceImpl extends BaseModelServiceImpl<FlowActuatorModel, FlowActuatorMapper, FlowActuatorFields, QueryParams> implements FlowActuatorService {

    /**
     * 云平台地址
     */
    @Value("${mp.devops.cp-url}")
    private String cpUrl;

    public FlowActuatorServiceImpl(FlowActuatorMapper stuMapper, FlowActuatorFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    /**
     * 删除虚拟机实例
     * @param actuatorId
     */
    @Override
    public void delExecutor(String actuatorId) {
        FlowActuatorModel flowActuatorModel = this.getModelMapper().get(actuatorId);
        if (Objects.isNull(flowActuatorModel)) {
            log.error("flowActuatorModel not exist for [{}]", actuatorId);
            return;
        }

        try {
            String delVmInstanceRsp = HttpUtils.sendDelete(cpUrl + "/v1/instances/" + flowActuatorModel.getInstanceId());
            JSONObject delVmInstanceObj = JSONObject.parseObject(delVmInstanceRsp);
            log.info("delete vm instance response:{}", delVmInstanceObj.toJSONString());
            if (!BaseConstants.SUCCESS_ZERO.equals(delVmInstanceObj.getString(BaseConstants.STR_CODE))) {
                log.error("delete vm instance error!");
            } else {
                log.info("delete vm instance success!");
            }
        } catch (Exception e) {
            log.error("delete vm instance error", e);
        }
    }

}
