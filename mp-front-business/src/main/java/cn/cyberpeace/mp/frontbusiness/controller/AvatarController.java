package cn.cyberpeace.mp.frontbusiness.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.service.AvatarService;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.deprecated.FileResponseData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@Api(value = "头像管理接口(业务)", tags = "AUTH-头像管理接口(业务)")
@Slf4j
@RestController("avatarFrontController")
@RequestMapping(ControllerConstant.BASE_API_PATH + "/avatar")
public class AvatarController {

    @Autowired
    private AvatarService avatarService;

    @ApiOperation(value = "上传头像，返回url", notes = "")
    @ApiImplicitParam(name = "file", value = "图片文件", dataTypeClass = MultipartFile.class)
    @PostMapping("/upload")
    @ResponseBody
    public ServiceResult<String> upload(MultipartFile file) {
        FileResponseData responseData = avatarService.upload(file);
        return ResultCommon.success(responseData.getHttpUrl());
    }


}
