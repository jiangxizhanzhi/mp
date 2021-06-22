package cn.cyberpeace.mp.devops.log.service.impl;

import cn.cyberpeace.mp.devops.log.enums.SiteTypeEnums;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import cn.cyberpeace.mp.devops.log.service.BasEsLogService;
import cn.cyberpeace.mp.devops.log.service.OperateLogService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * MP中台的操作日志
 * @author wanggang
 */
@Slf4j
@Service
public class MpOperateLogServiceImpl extends BasEsLogService implements OperateLogService {

    @Override
    public String create(OperationLogModel operationLogModel) {
        Map<String,Object> source = JSON.parseObject(JSON.toJSONString(operationLogModel), Map.class);
        return super.create("mp_operate_log",source);
    }


    @Override
    public void search() {

    }

    @Override
    public SiteTypeEnums siteType(){
        return SiteTypeEnums.MP;
    }
}
