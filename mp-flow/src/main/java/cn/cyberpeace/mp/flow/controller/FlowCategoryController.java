//// Copyright (C) 2024
//// All rights reserved.
//// Project name mp
//// Description:
//// Author:
//// File name FlowInfoController.java.
//// File size 15999.
//// File create date Fri Jul 12 12:55:30 CST 2024.
//// File update date Fri Jul 12 12:55:30 CST 2024.
//// Version 1.0.0
//package cn.cyberpeace.mp.flow.controller;
//
//import cn.cyberpeace.mp.base.controller.common.ResultCommon;
//import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
//import cn.cyberpeace.mp.base.service.result.ServiceResult;
//import cn.cyberpeace.mp.common.bo.OptionModelBO;
//import cn.cyberpeace.mp.common.model.OptionModel;
//import cn.cyberpeace.mp.common.request.queryparam.OptionQueryParams;
//import cn.cyberpeace.mp.common.service.OptionService;
//import cn.cyberpeace.mp.flow.constant.ControllerConstant;
//import cn.cyberpeace.mp.flow.constant.FlowConstant;
//import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
//import cn.cyberpeace.mp.flow.request.DeleteCategoryRequest;
//import cn.cyberpeace.mp.flow.service.impl.FlowInfoService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//@Slf4j
//@Api(tags = "流量资源分类管理")
//@RestController
//@RequestMapping(ControllerConstant.BASE_API_PATH + "/info")
//public class FlowCategoryController {
//
//    private static final String FLOW_CATEGORY = "flow_category";
//
//    @Autowired
//    private FlowInfoService flowServiceImpl;
//
//    @Resource(name = "sysOptionServiceImpl")
//    private OptionService optionService;
//
//    /**
//     * 分页查询流量分类列表
//     * @param queryParams
//     * @return
//     */
//    @ApiOperation("分页查询流量分类列表")
//    @GetMapping("/page_categories")
//    public ListObjServiceResult<OptionModelBO> pageCategories(OptionQueryParams queryParams) {
//        if (Objects.isNull(queryParams.getOrders()) && Objects.isNull(queryParams.getSort())) {
//            queryParams.setOrders(new String[]{"sort"});
//        }
//        queryParams.setType(FlowConstant.MP_COMMON_OPTION_TYPE_FLOW_CATEGORY);
//        ListObjServiceResult<OptionModelBO> result = optionService.page(queryParams);
//        return result;
//    }
//
//    /**
//     * 新增流量分类
//     * @param optionModelBO
//     * @return
//     */
//    @ApiOperation("新增流量分类")
//    @PostMapping("/add_category")
//    public ServiceResult addCategory(@RequestBody OptionModelBO optionModelBO) {
//        log.info("FlowInfoController.addCategory begin. optionModelBO:{}", optionModelBO);
//        optionModelBO.setType(FLOW_CATEGORY);
//        optionModelBO.setMeta("{\"activKeys\":\"no_auth\",\"visible\":1}");
//        optionModelBO.setCode(String.valueOf(optionService.getMaxCode(FLOW_CATEGORY) + 1));
//        optionModelBO.setSort(optionService.getMaxSort(FLOW_CATEGORY) + 1);
//        optionService.addOption(optionModelBO);
//        log.info("FlowInfoController.addCategory end.");
//        return ResultCommon.success();
//    }
//
//    /**
//     * 编辑流量分类
//     * @param optionModelBO
//     * @return
//     */
//    @ApiOperation("编辑流量分类")
//    @PostMapping("/edit_category")
//    public ServiceResult editCategory(@RequestBody OptionModelBO optionModelBO) {
//        log.info("FlowInfoController.editCategory begin. optionModelBO:{}", optionModelBO);
//        optionModelBO.setType(FLOW_CATEGORY);
//        optionService.editOption(optionModelBO);
//        log.info("FlowInfoController.editCategory end.");
//        return ResultCommon.success();
//    }
//
//    /**
//     * 删除流量分类
//     * @param request
//     * @return
//     */
//    @ApiOperation("删除流量分类")
//    @DeleteMapping("/delete_categories")
//    public ServiceResult deleteCategories(@RequestBody DeleteCategoryRequest request) {
//        log.info("FlowInfoController.deleteCategories begin. request:{}", request);
//        List<OptionModel> optionModels = optionService.findByIds(request.getIds());
//        List<String> codeList = new ArrayList<>();
//        for (OptionModel optionModel : optionModels) {
//            String code = optionModel.getCode();
//            codeList.add(code);
//        }
//        if (flowServiceImpl.checkCategoryUsed(codeList)) {
//            return ResultCommon.fail(FlowErrorCode.CATEGORY_HAS_USED);
//        }
//        optionService.deleteOptions(request.getIds());
//        log.info("FlowInfoController.deleteCategories end");
//        return ResultCommon.success();
//    }
//
//    /**
//     * 流量分类详情
//     * @param id
//     * @return
//     */
//    @ApiOperation("流量分类详情")
//    @ApiImplicitParam(name = "id", value = "分类id", dataTypeClass = String.class)
//    @GetMapping("/categories/detail")
//    public ServiceResult<OptionModelBO> findCategoriesById(@RequestParam String id) {
//        log.info("FlowInfoController.findCategoriesById begin. id:{}", id);
//        OptionModelBO optionModelBO = optionService.findById(id);
//        return new ServiceResult<>(optionModelBO).asResult();
//    }
//}
