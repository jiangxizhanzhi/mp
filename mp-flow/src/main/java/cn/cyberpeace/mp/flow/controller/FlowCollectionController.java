package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.constant.BaseConstants;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.model.FlowCollectionModel;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.request.AddFlowCollectionRelRequest;
import cn.cyberpeace.mp.flow.request.DelFlowCollectionRelRequest;
import cn.cyberpeace.mp.flow.request.queryparam.FlowCollectionQueryParams;
import cn.cyberpeace.mp.flow.request.queryparam.FlowCollectionRelQueryParams;
import cn.cyberpeace.mp.flow.service.FlowCollectionService;
import cn.cyberpeace.mp.flow.service.impl.FlowCollectionServiceImpl;
import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: 流量集合控制器
 * @Author qiaokang
 * @Date：2021/8/31 17:17
 */
@Slf4j
@Api(tags = "流量集合管理接口")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/collection")
public class FlowCollectionController extends BaseModelController<FlowCollectionModel, FlowCollectionQueryParams, FlowCollectionService> {

    @Autowired
    public FlowCollectionController(FlowCollectionService FlowCollectionService) {
        setModelService(FlowCollectionService);
    }

    @Autowired
    private FlowCollectionServiceImpl flowCollectionService;

    @ApiOperation("根据集合id查询流量信息")
    @GetMapping("/detail/page")
    public ListObjServiceResult<FlowInfoModel> getByCollectionId(FlowCollectionRelQueryParams queryParams) {
        if (StringUtils.isBlank(queryParams.getCollectionId())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        handleQueryParams(BaseConstants.STR_LIST, queryParams);
        return flowCollectionService.getByCollectionId(queryParams);
    }

    @ApiOperation("新增集合关联信息")
    @PostMapping("/collection_rel")
    public ServiceResult<Integer> addCollectionRel(@RequestBody @Validated AddFlowCollectionRelRequest request) {
        return flowCollectionService.addCollectionRel(request);
    }

    @ApiOperation("删除集合关联信息")
    @DeleteMapping("/collection_rel")
    public ServiceResult<Integer> delCollectionRel(@RequestBody @Validated DelFlowCollectionRelRequest request) {
        return flowCollectionService.delCollectionRel(request);
    }

}
