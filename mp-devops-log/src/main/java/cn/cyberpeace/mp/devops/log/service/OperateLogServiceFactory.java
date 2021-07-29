package cn.cyberpeace.mp.devops.log.service;

import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.enums.SiteTypeEnums;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import cn.cyberpeace.mp.devops.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.devops.log.request.OperationLogRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
     * 创建日志
     * @param request
     * @return
     */
    public IndexResponse create(OperationLogRequest request){
        OperationLogModel model = new OperationLogModel();
        model.setFrom(request.getFrom());
        model.setLogType(request.getLogType());
        model.setOpModule(Objects.nonNull(request.getOpModule()) ? OpModuleEnums.fromValue(request.getOpModule()).getDesc() : "");
        model.setOpType(Objects.nonNull(request.getOpType()) ? OpModuleEnums.fromValue(request.getOpType()).getDesc() : "");
        model.setOpTarget(request.getOpTarget());
        model.setTitle(request.getTitle());
        model.setUserId(request.getUserId());
        model.setUserName(request.getUserName());
        model.setUserType(request.getUserType());
        model.setDescription(request.getDescription());
        model.setOpIp(request.getOpIp());
        return this.create(model);
    }


    /**
     * 创建日志
     * @param operationLogModel
     */
    public IndexResponse create(OperationLogModel operationLogModel){
       return operateLogServiceMap.get(operationLogModel.getSiteType()).create(operationLogModel);
    }

    /**
     * 查询日志
     * @param request
     * @return
     */
    public SearchResponse search(OperationLogPageRequest request){
       return operateLogServiceMap.get(request.getSiteType()).search(request);
    }
}
