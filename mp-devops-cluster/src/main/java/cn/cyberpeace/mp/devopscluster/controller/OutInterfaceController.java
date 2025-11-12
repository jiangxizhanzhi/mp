package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.bo.RealEquipmentBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.converter.model.TypeAllConvertModel;
import cn.cyberpeace.mp.devopscluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devopscluster.request.ImagePageRequest;
import cn.cyberpeace.mp.devopscluster.request.PlatformPageRequest;
import cn.cyberpeace.mp.devopscluster.request.RealEquipmentPageRequest;
import cn.cyberpeace.mp.devopscluster.vo.ImagesPageVO;
import cn.cyberpeace.mp.devopscluster.vo.PlatformVO;
import cn.cyberpeace.mp.devopscluster.vo.RealEquipmentPageVO;
import cn.cyberpeace.mp.devopscluster.vo.common.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.cyberpeace.mp.base.constant.TargetEnum.REAL_EQUIPMENT_MANAGEMENT;
import static cn.cyberpeace.mp.base.constant.TargetEnum.REAL_MANAGE;

@Api(value = "对外接口列表", tags = "对外接口列表")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/out_interface")
@AuthTarget(TargetEnum.PLATFORM_MANAGEMENT)
public class OutInterfaceController {

    @Autowired
    private PlatformController platformController;

    @Autowired
    private ImagesController imagesController;

    @Autowired
    private RealEquipmentController realEquipmentController;

    @ApiOperation("查询超融合平台列表")
    @GetMapping("/page_platform")
    @AuthTarget(TargetEnum.NO_AUTH)
    public ServiceResult<PlatformVO> page(PlatformPageRequest pageRequest) {
        return platformController.page(pageRequest);
    }

    /**
     * 分页查询镜像列表
     */
    @AuthTarget(TargetEnum.IMAGE_MANAGE)
    @ApiOperation("分页查询")
    @GetMapping("/page_image")
    public PageResult<ImagesPageVO> page(ImagePageRequest request){
        return imagesController.page(request);
    }

    /**
     * 分页查询
     */
    @AuthTarget({TargetEnum.DEVOPS_EQUIPMENT_PAGE_GROUP, REAL_EQUIPMENT_MANAGEMENT, REAL_MANAGE})
    @ApiOperation("分页查询")
    @GetMapping("/page_real_equipment")
    public PageResult<RealEquipmentPageVO> page(RealEquipmentPageRequest request){
        return realEquipmentController.page(request);
    }

}
