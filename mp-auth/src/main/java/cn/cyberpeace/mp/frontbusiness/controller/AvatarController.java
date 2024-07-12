 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AvatarController.java.
 // File size 1999.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.service.AvatarService;
import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
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

/**
 * @ClassName AvatarController
 * @Description 头像管理接口
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 */
@Api(value = "用户管理接口", tags = "用户管理接口")
@Slf4j
@RestController("avatarFrontController")
@RequestMapping(ControllerConstant.BASE_API_PATH + "/avatar")
@AuthTarget({TargetEnum.PERSONAL_FOLDER, TargetEnum.USER_MANAGE})
public class AvatarController {

    @Autowired
    private AvatarService avatarService;

    /**
     * 上传用户头像
     *
     * @param file
     * @return
     */
    @ApiOperation(value = "上传用户头像(返回url)", notes = "")
    @ApiImplicitParam(name = "file", value = "图片文件", dataTypeClass = MultipartFile.class)
    @PostMapping("/upload")
    @ResponseBody
    public ServiceResult<String> upload(MultipartFile file) {
        FileResponseData responseData = avatarService.upload(file);
        return ResultCommon.success(responseData.getHttpUrl());
    }


}
