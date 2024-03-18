package cn.cyberpeace.mp.cscene.controller;


import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.scene.MultiSceneRegisterDto;
import cn.cyberpeace.mp.scene.core.bo.MultiSceneRegisterBo;
import cn.cyberpeace.mp.scene.service.CyberRangeHeartbeatService;
import cn.cyberpeace.mp.scene.service.ManageCenterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "多靶场相关接口", tags = "多靶场相关接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/multi-scene")
@RequiredArgsConstructor
public class MultiSceneController {


    private final ManageCenterService manageCenterService;
    private final CyberRangeHeartbeatService cyberRangeHeartbeatService;


    @ApiOperation(value = "接收分靶场heartbeat")
    @PostMapping("/receive/heartbeat")
    public ServiceResult<Boolean> receiveHeartbeat(@RequestBody @Validated MultiSceneRegisterDto multiSceneRegisterDto) {
        MultiSceneRegisterBo multiSceneRegisterBo = new MultiSceneRegisterBo();
        BeanUtils.copyProperties(multiSceneRegisterDto, multiSceneRegisterBo);

        manageCenterService.receiveRegisterOrHeartbeat(multiSceneRegisterBo);
        return ServiceResult.<Boolean>builder().source(Boolean.TRUE).build().asResult();
    }


}
