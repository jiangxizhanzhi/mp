package cn.cyberpeace.mp.front.business.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.service.AvatarService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.fdfsclient.FileResponseData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description: 头像管理接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "头像管理接口(业务)", tags = "AUTH-头像管理接口(业务)")
@Slf4j
@RestController("avatarFrontController")
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/avatar")
public class AvatarController {

    @Autowired
    private AvatarService avatarService;

    /*
    @ApiOperation(value = "查询所有头像", notes = "")
    @GetMapping("/all")
    public ServiceResult<List<AvatarModel>> getAll() {
        List<AvatarModel> avatars = avatarService.listAll();
        return ServiceResult.success(avatars);
    }
    */


    @ApiOperation(value = "上传头像，返回url", notes = "")
    @PostMapping("/upload")
    @ResponseBody
    public ServiceResult<String> upload(MultipartFile file) {
        FileResponseData responseData = avatarService.upload(file);
        return ServiceResult.success(responseData.getHttpUrl());
    }


}
