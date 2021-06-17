package cn.cyberpeace.mp.devops.log.service;

import cn.cyberpeace.mp.devops.log.enums.SiteTypeEnums;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wanggang
 */
@Component
public class OperateLogServiceFactory {

    private Map<SiteTypeEnums,OperateLogService> operateLogServiceMap = new ConcurrentHashMap();

    private List<OperateLogService> operateLogServiceList;

    @PostConstruct
    public void init(){
        operateLogServiceMap = operateLogServiceList.stream().collect(Collectors.toMap(OperateLogService::siteType, Function.identity()));
    }

    @Autowired
    public OperateLogServiceFactory(List<OperateLogService> operateLogServiceList) {
        this.operateLogServiceList = operateLogServiceList;
    }

    /**
     * 创建操作日志
     * @param operationLogModel
     */
    public Object create(OperationLogModel operationLogModel){
       return operateLogServiceMap.get(operationLogModel.getSiteType()).create(operationLogModel);
    }

    public void search(SiteTypeEnums siteTypeEnums){
        operateLogServiceMap.get(siteTypeEnums).search();
    }
}
