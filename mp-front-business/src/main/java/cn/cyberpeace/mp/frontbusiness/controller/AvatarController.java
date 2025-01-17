 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AvatarController.java.
 // File size 1707.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
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

 /**
  * 《阿凡达》控制器
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Api(value = "头像管理接口(业务)", tags = "AUTH-头像管理接口(业务)")
@Slf4j
@RestController("avatarFrontController")
@RequestMapping(ControllerConstant.BASE_API_PATH + "/avatar")
public class AvatarController {

     /**
      * 《阿凡达》服务
      */
     @Autowired
    private AvatarService avatarService;

     /**
      * 上传
      *
      * @param file 文件
      * @return {@link ServiceResult }<{@link String }>
      */
     @ApiOperation(value = "上传头像，返回url", notes = "")
    @ApiImplicitParam(name = "file", value = "图片文件", dataTypeClass = MultipartFile.class)
    @PostMapping("/upload")
    @ResponseBody
    public ServiceResult<String> upload(MultipartFile file) {
        FileResponseData responseData = avatarService.upload(file);
        return ResultCommon.success(responseData.getHttpUrl());
    }


}
