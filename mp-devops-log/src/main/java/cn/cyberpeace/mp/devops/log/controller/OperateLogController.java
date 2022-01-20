package cn.cyberpeace.mp.devops.log.controller;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.log.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.devops.log.request.OperationLogPageRequest;
import cn.cyberpeace.mp.devops.log.request.OperationLogRequest;
import cn.cyberpeace.mp.devops.log.response.OperationLogPageResponse;
import cn.cyberpeace.mp.devops.log.service.OperateLogServiceFactory;
import cn.cyberpeace.mp.devops.log.utils.SnUtils;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * 用户操作日志记录
 * @author wanggang
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/operate")
@Api(tags = "日志模块")
public class OperateLogController {

    private OperateLogServiceFactory operateLogServiceFactory;

    @Autowired
    public OperateLogController(OperateLogServiceFactory operateLogServiceFactory) {
        this.operateLogServiceFactory = operateLogServiceFactory;
    }

    @ApiOperation(value = "新增操作日志")
    @PostMapping("/add")
    public ServiceResult add(@RequestBody OperationLogRequest request, HttpServletRequest httpServletRequest){
        request.setOpIp(SnUtils.getRealRemoteIp(httpServletRequest));
        log.info("logRequest:{}", request);
        operateLogServiceFactory.create(request, LocaleContextHolder.getLocale());
        return ServiceResult.successful();
    }


    @ApiOperation(value = "查询操作日志")
    @GetMapping("/page")
    public ListObjServiceResult<OperationLogPageResponse> page(OperationLogPageRequest request) {
        Locale locale = LocaleContextHolder.getLocale();
        SearchResponse searchResponse = operateLogServiceFactory.search(request);
        SearchHits searchHits = searchResponse.getHits();
        List<OperationLogPageResponse> list = new ArrayList<>(searchHits.getHits().length);
        for (SearchHit hit : searchHits.getHits()) {
            OperationLogPageResponse operationLog = JSON.parseObject(hit.getSourceAsString(), OperationLogPageResponse.class);
            if (Objects.nonNull(operationLog) && Objects.nonNull(operationLog.getOpModuleID())) {
                OpModuleEnums opModuleEnums = OpModuleEnums.fromValue(operationLog.getOpModuleID());
                String opModuleCode = operateLogServiceFactory.getlocalizedMessage(locale, opModuleEnums.getCode());
                operationLog.setOpModule(opModuleCode);
            }
            if (Objects.nonNull(operationLog) && Objects.nonNull(operationLog.getOpTypeID())) {
                OpTypeEnums opTypeEnum = OpTypeEnums.fromValue(operationLog.getOpTypeID());
                String opTypeCode = operateLogServiceFactory.getlocalizedMessage(locale, opTypeEnum.getCode());
                operationLog.setOpType(opTypeCode);
            }
            list.add(operationLog);
        }
        return new ListObjServiceResult(new ListObjResult<>(list, Integer.valueOf(String.valueOf(searchHits.getTotalHits().value)))).asResult();

    }


    @GetMapping("test_exception_handler")
    public ServiceResult testErrorCode() {
        throw new IllegalArgumentException("illegal from mp.front.admin");
    }


}
