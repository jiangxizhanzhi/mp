package cn.cyberpeace.mp.devops.log.service.impl;

import cn.cyberpeace.mp.devops.log.bo.TargetLogBO;
import cn.cyberpeace.mp.devops.log.enums.OpTargetEnums;
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
            po.setType(operationLogModel.getOpType());
            po.setStatus("成功");
            po.setContent(operationLogModel.getContent());
            Map<String, Object> formMap = JSON.parseObject(JSON.toJSONString(po), Map.class);
            HttpUtil.post(hldLogApiUrl.concat("/testApi/zyLogVirtual/add"), formMap);
        }else if (operationLogModel.getOpTargetID().equals(OpTargetEnums.REALEQUIPMENT.ordinal())) {
            RealDeviceLogPO po = new RealDeviceLogPO();
            po.setUserName(operationLogModel.getUserName());
            if (Objects.nonNull(extInfo)) {
                po.setEquName(extInfo.getName());
                po.setEquType(extInfo.getEquType());
                po.setEquIp(extInfo.getEquIp());
            }
            po.setType(operationLogModel.getOpType());
            po.setStatus("成功");
            po.setContent(operationLogModel.getContent());
            Map<String, Object> formMap = JSON.parseObject(JSON.toJSONString(po), Map.class);
            HttpUtil.post(hldLogApiUrl.concat("/testApi/zyLogEquipment/add"), formMap);
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
