//// Copyright (C) 2024
//// All rights reserved.
//// Project name mp
//// Description:
//// Author:
//// File name FlowActuatorController.java.
//// File size 5620.
//// File create date Fri Jul 12 12:55:31 CST 2024.
//// File update date Fri Jul 12 12:55:31 CST 2024.
//// Version 1.0.0
//package cn.cyberpeace.mp.flow.controller;
//
//import cn.cyberpeace.mp.base.annotation.AuthTarget;
//import cn.cyberpeace.mp.base.constant.TargetEnum;
//import cn.cyberpeace.mp.base.controller.BaseModelController;
//import cn.cyberpeace.mp.base.controller.common.ResultCommon;
//import cn.cyberpeace.mp.base.service.result.ListObjResult;
//import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
//import cn.cyberpeace.mp.base.service.result.ServiceResult;
//import cn.cyberpeace.mp.flow.constant.BaseConstants;
//import cn.cyberpeace.mp.flow.constant.ControllerConstant;
//import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
//import cn.cyberpeace.mp.flow.model.FlowActuatorModel;
//import cn.cyberpeace.mp.flow.request.FlowActuatorRequest;
//import cn.cyberpeace.mp.flow.request.ListSceneRequest;
//import cn.cyberpeace.mp.flow.request.queryparam.FlowActuatorQueryParams;
//import cn.cyberpeace.mp.flow.response.FlowActuatorResponse;
//import cn.cyberpeace.mp.flow.response.SceneInstanceResponse;
//import cn.cyberpeace.mp.flow.service.FlowActuatorService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Objects;
//import java.util.Set;
//
///**
// * @ClassName FlowActuatorController
// * @Description 流量发生器管理
// * @Author maojun
// * @Date 2024/07/02
// * @Version 1.0.0
// * @see BaseModelController
// */
//@Slf4j
//@Api(tags = "流量发生器接口")
//@RestController
//@RequestMapping(ControllerConstant.BASE_API_PATH + "/actuator")
//@AuthTarget({TargetEnum.DYNAMIC_TRAFFIC_LIST, TargetEnum.STATIC_TRAFFIC_LIST, TargetEnum.TRAFFIC_SCHEME_LIST})
//public class FlowActuatorController extends BaseModelController<FlowActuatorModel, FlowActuatorQueryParams, FlowActuatorService> {
//
//    @Resource(name = "flowActuatorServiceImpl")
//    private FlowActuatorService flowActuatorService;
//
//    /**
//     * listSceneByFuzzy
//     * @param request
//     * @return
//     */
//    @AuthTarget({TargetEnum.STATIC_TRAFFIC_LIST, TargetEnum.FLOW_GROUP})
//    @ApiOperation("模糊搜索场景信息")
//    @PostMapping("/list_scene")
//    public ListObjServiceResult<SceneInstanceResponse> listSceneByFuzzy(@RequestBody ListSceneRequest request) {
//        // 创建FlowActuatorQueryParams实例，用于设置分页查询参数
//        FlowActuatorQueryParams params = new FlowActuatorQueryParams();
//        // 设置查询的页码
//        params.setPage(1);
//        // 设置每页显示的条数
//        params.setPerPage(50);
//        // 调用flowActuatorService的page方法，根据设置的参数进行分页查询
//        var result = flowActuatorService.page(params);
//
//        // 初始化一个空列表，用于存储转换后的场景实例响应对象
//        List<SceneInstanceResponse> responseList = new ArrayList<>();
//        // 遍历查询结果中的每个对象
//        Set<String> names = new HashSet<>();
//        for (FlowActuatorResponse response : result.getSource().getObjs()) {
//            if (StringUtils.isNotEmpty(response.getSceneName()) && !names.contains(response.getSceneName())) {
//                // 创建一个新的SceneInstanceResponse实例
//                SceneInstanceResponse r = new SceneInstanceResponse();
//                // 将name赋值给id ， 后面前端用id来查询的时候实际上是使用name的值进行查询
//                r.setId(response.getSceneName());
//                // 将查询结果中的场景名称赋值给新的实例
//                r.setName(response.getSceneName());
//                // 将新的实例添加到列表中
//                responseList.add(r);
//                names.add(response.getSceneName());
//            }
//        }
//        // 创建并返回一个服务结果对象，其中包含转换后的场景实例列表及其总数
//        return new ListObjServiceResult<>(new ListObjResult<>(responseList, responseList.size())).asResult();
//
//    }
//
//
//    /**
//     * batchStart
//     * @param request 流量发生器id列表
//     * @return 统一服务返回结果
//     */
//    @AuthTarget({TargetEnum.DYNAMIC_TRAFFIC_LIST, TargetEnum.STATIC_TRAFFIC_LIST})
//    @ApiOperation("启动流量发生器")
//    @PostMapping("/batch_start")
//    public ServiceResult batchStart(@RequestBody FlowActuatorRequest request) {
//        // 参数校验
//        if (Objects.isNull(request.getIds())) {
//            return ResultCommon.fail(FlowErrorCode.IDS_CAN_NOT_EMPTY);
//        }
//        // 启动流量发生器
//        flowActuatorService.batchStart(request.getIds());
//        return ResultCommon.success();
//    }
//
//    /**
//     * batchPause
//     * @param request 流量发生器id列表
//     * @return 统一服务返回结果
//     */
//    @AuthTarget({TargetEnum.DYNAMIC_TRAFFIC_LIST, TargetEnum.STATIC_TRAFFIC_LIST})
//    @ApiOperation("暂停流量发生器")
//    @PostMapping("/batch_pause")
//    public ServiceResult batchPause(@RequestBody FlowActuatorRequest request) {
//        // 参数校验
//        if (Objects.isNull(request.getIds())) {
//            return ResultCommon.fail(FlowErrorCode.IDS_CAN_NOT_EMPTY);
//        }
//        // 暂停流量发生器
//        flowActuatorService.batchPause(request.getIds());
//        return ResultCommon.success();
//    }
//
//    /**
//     * batchReboot
//     * @param request 流量发生器id列表
//     * @return 统一服务返回结果
//     */
//    @AuthTarget({TargetEnum.DYNAMIC_TRAFFIC_LIST, TargetEnum.STATIC_TRAFFIC_LIST})
//    @ApiOperation("重启流量发生器")
//    @PostMapping("/batch_reboot")
//    public ServiceResult batchReboot(@RequestBody FlowActuatorRequest request) {
//        // 参数校验
//        if (Objects.isNull(request.getIds())) {
//            return ResultCommon.fail(FlowErrorCode.IDS_CAN_NOT_EMPTY);
//        }
//        // 重启流量发生器
//        flowActuatorService.batchReboot(request.getIds());
//        return ResultCommon.success();
//    }
//}
