package cn.cyberpeace.mp.devops.log.service.impl;

import cn.cyberpeace.mp.devops.log.bo.TargetLogBO;
import cn.cyberpeace.mp.devops.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.devops.log.enums.SiteTypeEnums;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import cn.cyberpeace.mp.devops.log.po.RealDeviceLogPO;
import cn.cyberpeace.mp.devops.log.po.VirtualDeviceLogPO;
import cn.cyberpeace.mp.devops.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.devops.log.service.OperateLogService;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 对接HLD的日志管理系统
 * @author wanggang
 */
@Slf4j
@Service
public class HldOperateLogServiceImpl implements OperateLogService {

    @Value("${mp.hld.log-api-url}")
    private String hldLogApiUrl;

    private static final Map<OpTypeEnums,String> VIRTUAL_TYPE_MAP = new HashMap<>();
    private static final Map<OpTypeEnums,String> EQUIPMENT_TYPE_MAP = new HashMap<>();

    static {
        VIRTUAL_TYPE_MAP.put(OpTypeEnums.LOGIN,"09");
        VIRTUAL_TYPE_MAP.put(OpTypeEnums.CREATE,"01");
        VIRTUAL_TYPE_MAP.put(OpTypeEnums.UPDATE,"02");
        VIRTUAL_TYPE_MAP.put(OpTypeEnums.DELETE,"03");
        VIRTUAL_TYPE_MAP.put(OpTypeEnums.EXPORT,"04");
        VIRTUAL_TYPE_MAP.put(OpTypeEnums.START,"07");
        VIRTUAL_TYPE_MAP.put(OpTypeEnums.STOP,"08");


        EQUIPMENT_TYPE_MAP.put(OpTypeEnums.CREATE,"01");
        EQUIPMENT_TYPE_MAP.put(OpTypeEnums.UPDATE,"02");
        EQUIPMENT_TYPE_MAP.put(OpTypeEnums.DELETE,"03");
        EQUIPMENT_TYPE_MAP.put(OpTypeEnums.LOGIN,"04");

    }


    @Override
    public void create(OperationLogModel operationLogModel) {
        final TargetLogBO extInfo = (TargetLogBO)operationLogModel.getExtInfo();
        if (operationLogModel.getOpTargetID().equals(OpTargetEnums.IMAGE.ordinal())) {
            VirtualDeviceLogPO po = new VirtualDeviceLogPO();
            po.setUserName(operationLogModel.getUserName());
            if (Objects.nonNull(extInfo)) {
                po.setXnjId(extInfo.getUuid());
                po.setXnjName(extInfo.getName());
            }
            po.setType(VIRTUAL_TYPE_MAP.get(OpTypeEnums.fromValue(operationLogModel.getOpTypeID())));
            po.setStatus("成功");
            po.setContent(operationLogModel.getContent());
            Map<String, Object> formMap = JSON.parseObject(JSON.toJSONString(po), Map.class);
            String url = hldLogApiUrl.concat("/testApi/zyLogVirtual/add");
            log.info("=======>>>POST LOG URL:{},params:{}",url,formMap);
            HttpUtil.post(hldLogApiUrl.concat("/testApi/zyLogVirtual/add"), formMap);
        }else if (operationLogModel.getOpTargetID().equals(OpTargetEnums.REALEQUIPMENT.ordinal())) {
            RealDeviceLogPO po = new RealDeviceLogPO();
            po.setUserName(operationLogModel.getUserName());
            if (Objects.nonNull(extInfo)) {
                po.setEquName(extInfo.getName());
                po.setEquType(extInfo.getEquType());
                po.setEquIp(extInfo.getEquIp());
            }
            po.setType(EQUIPMENT_TYPE_MAP.get(OpTypeEnums.fromValue(operationLogModel.getOpTypeID())));
            po.setStatus("成功");
            po.setContent(operationLogModel.getContent());
            Map<String, Object> formMap = JSON.parseObject(JSON.toJSONString(po), Map.class);
            String url = hldLogApiUrl.concat("/testApi/zyLogEquipment/add");
            log.info("=======>>>POST LOG URL:{},params:{}",url,formMap);
            HttpUtil.post(url, formMap);
        }
    }

    @Override
    public SearchResponse search(OperationLogPageRequest request) {
        return null;
    }

    @Override
    public SiteTypeEnums siteType(){
        return SiteTypeEnums.HLD;
    }


}
