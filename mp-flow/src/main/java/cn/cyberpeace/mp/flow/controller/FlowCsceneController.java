package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.model.CsceneConfigFlowTaskModel;
import cn.cyberpeace.mp.flow.repository.CsceneConfigFlowTaskMapper;
import cn.cyberpeace.mp.flow.request.CsceneConfigFlowTaskRequest;
import cn.cyberpeace.mp.flow.request.CsceneFlowTaskRequest;
import cn.cyberpeace.mp.flow.response.CsceneFlowTaskResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Api(tags = "场景流量")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/cscene")
public class FlowCsceneController {

    @Resource
    private CsceneConfigFlowTaskMapper csceneConfigFlowTaskMapper;

    @ApiOperation("分页查询场景模板中的已配置的流量任务")
    @GetMapping("/task/list")
    public ListObjServiceResult<CsceneFlowTaskResponse> list(CsceneConfigFlowTaskRequest request) {
        var condition = new HumanCondition()
                .filterEqual(CsceneConfigFlowTaskModel::getCsceneId, request.getId())
                .filterEqual(CsceneConfigFlowTaskModel::getCsceneNodeId, request.getNodeId())
                .pagination(request.getPage(), request.getPageSize());
        var total = csceneConfigFlowTaskMapper.total(condition);
        var list = csceneConfigFlowTaskMapper.list(condition);
        List<CsceneFlowTaskResponse> result = new ArrayList<>();
        for (var m : list) {
            CsceneFlowTaskResponse response = JsonCommon.parse(m.getContent(), CsceneFlowTaskResponse.class);
            response.setTaskId(m.getId());
            response.setCsceneId(m.getCsceneId());
            response.setCsceneNodeId(m.getCsceneNodeId());
            result.add(response);
        }
        return new ListObjServiceResult<>(new ListObjResult<>(result, total)).asResult();
    }

    @ApiOperation("新增场景模板中的流量任务")
    @PostMapping("/task/add")
    public ListObjServiceResult<CsceneFlowTaskResponse> add(@RequestBody CsceneFlowTaskRequest request) {

        var condition = new HumanCondition()
                .filterEqual(CsceneConfigFlowTaskModel::getCsceneId, request.getId())
                .filterEqual(CsceneConfigFlowTaskModel::getCsceneNodeId, request.getNodeId());
        csceneConfigFlowTaskMapper.filterDelete(condition);

        List<CsceneConfigFlowTaskModel> models = new ArrayList<>();
        for (var task : request.getTasks()) {
            CsceneConfigFlowTaskModel model = new CsceneConfigFlowTaskModel();
            model.setContent(JsonCommon.stringify(task));
            model.setCsceneId(request.getId());
            model.setCsceneNodeId(request.getNodeId());
            models.add(model);
        }
        csceneConfigFlowTaskMapper.bulkCreate(models);

        List<CsceneFlowTaskResponse> result = new ArrayList<>();
        for (var m : models) {
            CsceneFlowTaskResponse response = JsonCommon.parse(m.getContent(), CsceneFlowTaskResponse.class);
            response.setTaskId(m.getId());
            response.setCsceneId(m.getCsceneId());
            response.setCsceneNodeId(m.getCsceneNodeId());
            result.add(response);
        }
        return new ListObjServiceResult<>(new ListObjResult<>(result, result.size())).asResult();
    }
}
