package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.excel.ImportUser;
import cn.cyberpeace.mp.auth.model.AvatarModel;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.SysConfigModel;
import cn.cyberpeace.mp.auth.service.AvatarService;
import cn.cyberpeace.mp.auth.service.SysConfigService;
import cn.cyberpeace.mp.auth.service.UploadDataListener;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.controller.stereotype.UnlimitPagination;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.fdfsclient.FileResponseData;
import com.alibaba.excel.EasyExcel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Api(value = "头像管理接口", tags = "头像管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_UNAUTH_API_PATH + "/avatar")
public class AvatarController extends BaseModelController<AvatarModel, QueryParams, AvatarService> {

    @Autowired
    public AvatarController(AvatarService modelService) {
        setModelService(modelService);
    }

    @ApiOperation(value = "查询所有默认头像", notes = "")
    @GetMapping("/all")
    public ServiceResult getAll() {
        List<AvatarModel> avatars = getModelService().listAll();
        return ResultUtils.commonResult(true, avatars);
    }


    @ApiOperation(value = "上传用户头像，返回url", notes = "")
    @PostMapping("/upload")
    @ResponseBody
    public ServiceResult upload(MultipartFile file) {
        FileResponseData responseData = getModelService().upload(file);
        return ResultUtils.commonResult(true, responseData.getHttpUrl());
    }


}
