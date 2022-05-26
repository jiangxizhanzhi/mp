package cn.cyberpeace.mp.flow.service.impl;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.model.FlowScriptDemoModel;
import cn.cyberpeace.mp.flow.repository.FlowScriptDemoMapper;
import cn.cyberpeace.mp.flow.response.QueryFlowScriptDemoResponse;
import cn.cyberpeace.mp.flow.service.FlowScriptDemoService;
import cn.cyberpeace.mp.flow.service.field.FlowScriptDemoFields;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 流量脚本模板service
 * @Author qiaokang
 * @Date：2021/6/10 10:31
 */
@Service
@Slf4j
public class FlowScriptDemoServiceImpl extends BaseModelServiceImpl<FlowScriptDemoModel, FlowScriptDemoMapper,
        FlowScriptDemoFields, QueryParams> implements FlowScriptDemoService {

    @Autowired
    private BaseAppConfig baseAppConfig;

    public FlowScriptDemoServiceImpl(FlowScriptDemoMapper stuMapper, FlowScriptDemoFields stuFields) {
        setModelFields(stuFields);
        setModelMapper(stuMapper);
    }

    /**
     * 查询所有脚本模板
     * @return
     */
    public List<FlowScriptDemoModel> listAll() {
        return getModelMapper().listAll();
    }

    /**
     * 查询脚本模板信息
     * @return
     */
    public ServiceResult<QueryFlowScriptDemoResponse> listFlowScriptDemos() {
        QueryFlowScriptDemoResponse response = new QueryFlowScriptDemoResponse();
        List<QueryFlowScriptDemoResponse.ScriptDemo> scriptDemoList = new ArrayList<>();

        List<FlowScriptDemoModel> flowScriptDemoModels = getModelMapper().listAll();

        if (CollectionUtils.isNotEmpty(flowScriptDemoModels)) {
            scriptDemoList = flowScriptDemoModels.stream().map(demo -> new QueryFlowScriptDemoResponse.ScriptDemo(demo.getUrl(),
                    baseAppConfig.getFdfsOuterHttpServer() + File.separator + demo.getUrl(), demo.getScriptName())).collect(Collectors.toList());
        }
        response.setDemoList(scriptDemoList);

        return ServiceResult.success(response);
    }

}
