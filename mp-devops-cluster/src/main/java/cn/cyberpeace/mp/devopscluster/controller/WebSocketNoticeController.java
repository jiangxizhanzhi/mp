package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.message.FormatMessage;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtImageInfo;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtSystemInfo;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtVm;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.ImagesService;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.ImagesConverter;
import cn.cyberpeace.mp.devopscluster.converter.model.ImageConvertModel;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.common.TargetConnection;
import cn.cyberpeace.mp.devopscluster.utils.JsonUtils;
import cn.cyberpeace.mp.devopscluster.vo.ImagesPageVO;
import cn.cyberpeace.mp.devopscluster.ws.ImagesByUuidSocketServer;
import cn.cyberpeace.mp.devopscluster.ws.ImagesConnectSocketServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Objects;


@Validated
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/ws")
@Api(tags = "WS通知管理")
@RequiredArgsConstructor
public class WebSocketNoticeController {

    private final ImagesService imagesService;

    private final ImagesConverter imagesConverter;

    private final ImagesConnectSocketServer imagesConnectSocketServer;

    private final ImagesByUuidSocketServer imagesByUuidSocketServer;

    @ApiOperation("根据镜像UUID获取登录链接信息")
    @ApiImplicitParam(name = "uuid", value = "靶标id", dataTypeClass = String.class)
    @GetMapping("/images/connection")
    public ServiceResult connection(@RequestParam String uuid){
        CloudPlatformPageResult<TargetConnection> connection = imagesService.connection(uuid);
        imagesConnectSocketServer.batchSendInfo(JsonUtils.toUnderlineJSONString(new FormatMessage<>(connection.getList(),"MP-image-connection")), Arrays.asList(uuid));
        return ResultCommon.success();
    }

    @ApiOperation("根据镜像UUID获取靶标信息")
    @ApiImplicitParam(name = "uuid", value = "靶标id", dataTypeClass = String.class)
    @GetMapping("/device/get-by-uuid")
    public ServiceResult getByUuid(@RequestParam String uuid){
        ImageConvertModel convertModel = imagesConverter.findByUuid(uuid);
        ExtSystemInfo extSystemInfo = convertModel.getExtSystemInfo();
        ExtVm extVm = convertModel.getExtVm();
        ExtImageInfo extImageInfo = convertModel.getExtImageInfo();
        ImagesPageVO pageVO = ImagesPageVO.builder().uuid(convertModel.getUuid()).name(convertModel.getName())
                .logo(convertModel.getLogo())
                .mainCategory(convertModel.getMainCategory()).subCategory(convertModel.getSubCategory()).thirdCategory(convertModel.getThirdCategory())
                .imageType(convertModel.getImageType()).isImport(convertModel.getIsImport()).isInternal(convertModel.getIsInternal())
                .systemType(Objects.nonNull(extSystemInfo) ? extSystemInfo.getSystemType() : null)
                .flavor(Objects.nonNull(extVm) ? extVm.getFlavor() : "")
                .description(convertModel.getDescription()).imageStatus(Objects.nonNull(extImageInfo) ? extImageInfo.getImageStatus() : null)
                .authTotal(convertModel.getAuthTotal()).isPublic(convertModel.getIsPublic()).extSystemInfo(convertModel.getExtSystemInfo())
                .extGateway(convertModel.getExtGateway()).extVm(convertModel.getExtVm()).extImageInfo(convertModel.getExtImageInfo())
                .extDocker(convertModel.getExtDocker()).build();
        imagesByUuidSocketServer.batchSendInfo(JsonUtils.toUnderlineJSONString(new FormatMessage<>(pageVO,"MP-device-by-uuid")));
        return ResultCommon.success();
    }

}
