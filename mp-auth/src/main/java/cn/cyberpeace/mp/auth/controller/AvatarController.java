package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.AvatarModel;
import cn.cyberpeace.mp.auth.service.AvatarService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.fdfsclient.FileResponseData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(value = "头像管理接口", tags = "AUTH-头像管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/avatar")
public class AvatarController extends BaseModelController<AvatarModel, QueryParams, AvatarService> {

    @Autowired
    public AvatarController(AvatarService modelService) {
        setModelService(modelService);
    }

    @ApiOperation(value = "查询所有头像", notes = "")
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult getAll() {
        List<AvatarModel> avatars = getModelService().listAll();
        return ResultUtils.commonResult(true, avatars);
    }


    @ApiOperation(value = "上传头像，返回url", notes = "")
    @PostMapping("/upload")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult upload(MultipartFile file) {
        FileResponseData responseData = getModelService().upload(file);
        return ResultUtils.commonResult(true, responseData.getHttpUrl());
    }


}
