 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowScriptDemoServiceImpl.java.
 // File size 2907.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.service.impl;

 import cn.cyberpeace.mp.base.config.BaseAppConfig;
 import cn.cyberpeace.mp.base.controller.common.ResultCommon;
 import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
 import cn.cyberpeace.mp.base.service.param.QueryParams;
 import cn.cyberpeace.mp.base.service.result.ServiceResult;
 import cn.cyberpeace.mp.flow.model.FlowScriptDemoModel;
 import cn.cyberpeace.mp.flow.repository.FlowScriptDemoMapper;
 import cn.cyberpeace.mp.flow.response.QueryFlowScriptDemoResponse;
 import cn.cyberpeace.mp.flow.service.field.FlowScriptDemoFields;
 import lombok.extern.slf4j.Slf4j;
 import org.apache.commons.collections4.CollectionUtils;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.stereotype.Service;

 import java.io.File;
 import java.util.ArrayList;
 import java.util.List;

/**
 * @ClassName FlowScriptDemoServiceImpl
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelServiceImpl
 */
@Service
@Slf4j
public class FlowScriptDemoService extends BaseModelServiceImpl<FlowScriptDemoModel, FlowScriptDemoMapper,
        FlowScriptDemoFields, QueryParams> {

    @Autowired
    private BaseAppConfig baseAppConfig;

    public FlowScriptDemoService(FlowScriptDemoMapper stuMapper, FlowScriptDemoFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    /**
     * 查询脚本模板信息
     * @return
     */
    public ServiceResult<QueryFlowScriptDemoResponse> listScriptDemos(Integer type) {
        QueryFlowScriptDemoResponse response = new QueryFlowScriptDemoResponse();
        List<QueryFlowScriptDemoResponse.ScriptDemo> scriptDemoList = new ArrayList<>();

        List<FlowScriptDemoModel> flowScriptDemoModels = getModelMapper().listAll(type);

        if (CollectionUtils.isNotEmpty(flowScriptDemoModels)) {
            List<QueryFlowScriptDemoResponse.ScriptDemo> list = new ArrayList<>();
            for (FlowScriptDemoModel demo : flowScriptDemoModels) {
                QueryFlowScriptDemoResponse.ScriptDemo scriptDemo = new QueryFlowScriptDemoResponse.ScriptDemo(demo.getUrl(),
                        baseAppConfig.getFdfsOuterHttpServer() + File.separator + demo.getUrl(), demo.getScriptName());
                list.add(scriptDemo);
            }
            scriptDemoList = list;
        }
        response.setDemoList(scriptDemoList);

        return ResultCommon.success(response);
    }

}
