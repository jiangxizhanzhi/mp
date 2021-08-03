package cn.cyberpeace.mp.devops.log.service;

import cn.cyberpeace.mp.devops.log.annotation.OperateLog;
import cn.cyberpeace.mp.devops.log.constant.LogConstant;
import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.devops.log.enums.SiteTypeEnums;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import cn.cyberpeace.mp.devops.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.devops.log.request.OperationLogRequest;
import cn.cyberpeace.mp.devops.log.utils.SnUtils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
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
    @Async
    public IndexResponse create(OperationLogRequest request){
        OperationLogModel model = new OperationLogModel();
        model.setFrom(request.getFrom());
        model.setLogType(request.getLogType());
        model.setOpModule(Objects.nonNull(request.getOpModule()) ? OpModuleEnums.fromValue(request.getOpModule()).getDesc() : "");
        model.setOpType(Objects.nonNull(request.getOpType()) ? OpTypeEnums.fromValue(request.getOpType()).getDesc() : "");
        model.setOpTarget(request.getOpTarget());
        model.setTitle(request.getTitle());
        model.setUserId(request.getUserId());
        model.setUserName(request.getUserName());
        model.setUserType(request.getUserType());
        model.setDescription(request.getDescription());
        model.setOpIp(request.getOpIp());
        model.setContent(request.getContent());

        processDefaultContent(model);
        return this.create(model);
    }

    /**
     * 创建日志
     * @param operateLog
     * @return
     */
    @Async
    public IndexResponse create(OperateLog operateLog,HttpServletRequest request) {

        Object userId = request.getAttribute(LogConstant.USER_ID_KEY);
        Object userName = request.getAttribute(LogConstant.USER_NAME_KEY);
        Object userType = request.getAttribute(LogConstant.USER_TYPE_KEY);

        OperationLogModel model = new OperationLogModel();
        if (Objects.nonNull(userId)) {
            model.setUserId(userId.toString());
        }

        if (Objects.nonNull(userName)) {
            model.setUserName(userName.toString());
        }

        if (Objects.nonNull(userType)) {
            model.setUserType(userType.toString());
        }

        //站点类型
        model.setSiteType(SiteTypeEnums.MP);
        //操作模块
        model.setOpModule(operateLog.module().getDesc());
        //操作类型
        model.setOpType(operateLog.type().getDesc());
        //操作对象
        model.setOpTarget(operateLog.target());
        //操作主题
        model.setTitle(operateLog.title());
        //操作描述
        model.setDescription(operateLog.desc());
        //记录操作时间
        model.setOpTime(LocalDateTime.now());
        //请求IP
        model.setOpIp(SnUtils.getRealRemoteIp(request));

        return this.create(model);
    }


    /**
     * 创建日志
     * @param operationLogModel
     */
    private IndexResponse create(OperationLogModel operationLogModel){
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

    private void processDefaultContent(OperationLogModel model) {
        if (StringUtils.isNotBlank(model.getContent())) {
            return;
        }

        String content = model.getOpType() + ":" + model.getOpModule() + ", IP地址: " + model.getOpIp();
        model.setContent(content);
        model.setOpType(OpTypeEnums.EXAM.getDesc());
    }
}
