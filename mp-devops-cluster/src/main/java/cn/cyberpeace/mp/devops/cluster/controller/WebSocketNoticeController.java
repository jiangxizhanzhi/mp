package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.message.FormatMessage;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.ImagesService;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.model.common.TargetConnection;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import cn.cyberpeace.mp.devops.cluster.ws.ImagesConnectSocketServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * WS通知管理
 */
@Validated
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/ws")
@Api(tags = "WS通知管理")
@RequiredArgsConstructor
public class WebSocketNoticeController {

    private final ImagesService imagesService;

    private final ImagesConnectSocketServer imagesConnectSocketServer;

    @ApiOperation("根据镜像UUID获取登录链接信息")
    @GetMapping("/images/connection")
    public ServiceResult connection(@RequestParam String uuid){
        CloudPlatformPageResult<TargetConnection> connection = imagesService.connection(uuid);
        imagesConnectSocketServer.batchSendInfo(JsonUtils.toUnderlineJSONString(new FormatMessage<>(connection.getList(),"MP-image-connection")), Arrays.asList(uuid));
        return ServiceResult.successful();
    }

}
