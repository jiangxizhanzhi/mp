package cn.cyberpeace.mp.flow.v2.controller;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.controller.BaseModelBulkDeleteBOController;
import cn.cyberpeace.mp.base.controller.BaseModelCreateBOController;
import cn.cyberpeace.mp.base.controller.BaseModelGetBOController;
import cn.cyberpeace.mp.base.controller.BaseModelListBOController;
import cn.cyberpeace.mp.base.controller.BaseModelUpdateBOController;
import cn.cyberpeace.mp.base.controller.stereotype.ModelBOClass;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.v2.bo.EvasionProfileBO;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskBO;
import cn.cyberpeace.mp.flow.v2.model.FlowModel;
import cn.cyberpeace.mp.flow.v2.model.FlowTaskModel;
import cn.cyberpeace.mp.flow.v2.repository.FlowMapper;
import cn.cyberpeace.mp.flow.v2.service.FlowTaskService;
import cn.cyberpeace.mp.flow.v2.service.param.FlowTaskQueryParams;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(value = "流量任务管理接口", tags = "流量任务管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/flow_tasks")
@ModelBOClass(FlowTaskBO.class)
public class FlowTaskController implements
        BaseModelListBOController<FlowTaskBO, FlowTaskModel, FlowTaskQueryParams, FlowTaskService>,
        BaseModelGetBOController<FlowTaskBO, FlowTaskModel, FlowTaskService>,
        BaseModelCreateBOController<FlowTaskBO, FlowTaskModel, FlowTaskService>,
        BaseModelUpdateBOController<FlowTaskBO, FlowTaskModel, FlowTaskService>,
        BaseModelBulkDeleteBOController<FlowTaskBO, FlowTaskModel, FlowTaskService> {

    @Getter
    @Setter
    private FlowTaskService modelService;

    @Autowired
    public FlowTaskController(FlowTaskService modelService) {
        setModelService(modelService);
    }

    @ApiOperation("新增流量任务")
    @Override
    public ObjServiceResult<FlowTaskBO> create(@Validated FlowTaskBO bo, String[] fields) {
        FlowMapper flowMapper = BeanCommon.getBean(FlowMapper.class);
        if (CollectionUtils.isNotEmpty(bo.getFlows())) {
            List<String> ids = bo.getFlows().stream().map(f -> f.getId()).collect(Collectors.toList());
            var dbFlow = flowMapper.list(new HumanCondition()
                    .filterIn(FlowModel::getId, ids.toArray(new String[]{}))
            );
            Map<String, FlowModel> results = new HashMap<>();
            dbFlow.forEach(f -> {
                results.put(f.getId(), f);
            });
            bo.getFlows().forEach(f -> {
                var model = results.get(f.getId());
                if (model != null) {
                    f.setSupportEvasion(model.getSupportEvasion());
                    f.setPcapFile(model.getPcapFile());
                    f.setResourceType(model.getResourceType());
                    f.setProtocols(model.getProtocols());
                }
            });
        }
        return BaseModelCreateBOController.super.create(bo, fields);
    }

    @ApiOperation("根据id获取流量任务")
    @Override
    public ObjServiceResult<FlowTaskBO> get(String id, String[] fields) {
        ObjServiceResult<FlowTaskBO> boResult = BaseModelGetBOController.super.get(id, fields);
        getModelService().fillFlowInfo(boResult.getData());

        return boResult;
    }

    @GetMapping("/get_evasion_profiles")
    public ServiceResult<EvasionProfileBO> getEvasionProfiles() {
        EvasionProfileBO evasionProfileBO = new EvasionProfileBO();
        evasionProfileBO.setEvasionProfiles(new ArrayList<>());
        {
            String key = "Browser: High Evasion";
            EvasionProfileBO.Profile profile = new EvasionProfileBO.Profile();
            profile.setName(key);
            profile.setValue(key);
            evasionProfileBO.getEvasionProfiles().add(profile);
        }
        {
            String key = "Browser: Low Evasion";
            EvasionProfileBO.Profile profile = new EvasionProfileBO.Profile();
            profile.setName(key);
            profile.setValue(key);
            evasionProfileBO.getEvasionProfiles().add(profile);
        }
        {
            String key = "DCERPC: High Evasion";
            EvasionProfileBO.Profile profile = new EvasionProfileBO.Profile();
            profile.setName(key);
            profile.setValue(key);
            evasionProfileBO.getEvasionProfiles().add(profile);
        }
        {
            String key = "DCERPC: Medium Evasion";
            EvasionProfileBO.Profile profile = new EvasionProfileBO.Profile();
            profile.setName(key);
            profile.setValue(key);
            evasionProfileBO.getEvasionProfiles().add(profile);
        }
        return new ServiceResult<EvasionProfileBO>(evasionProfileBO);
    }
}
