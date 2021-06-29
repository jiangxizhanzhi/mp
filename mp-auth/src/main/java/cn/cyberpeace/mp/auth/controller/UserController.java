package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.ResetPwdRequest;
import cn.cyberpeace.mp.auth.dto.ResetSelfPwdRequest;
import cn.cyberpeace.mp.auth.dto.validate.AddGroup;
import cn.cyberpeace.mp.auth.dto.validate.UpdateGroup;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.param.UserQueryParams;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "用户管理接口", tags = "UserController")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/users")
public class UserController extends BaseModelController<UserModel, UserQueryParams, UserService> {

    @Autowired
    public UserController(UserService modelService) {
        setModelService(modelService);
    }


    @Autowired
    private AuthConfig authConfig;


    @ApiOperation(value = "修改密码", notes = "")
    @PostMapping("/resetpwd")
    public ServiceResult resetPwd(@Validated(AddGroup.class) @RequestBody ResetPwdRequest resetPwdRequest) {
        String decryptedPwd = PasswordUtils.desDecrypt(resetPwdRequest.getNewPwd());
        boolean result = getModelService().resetPwd(resetPwdRequest.getUserId(), decryptedPwd);
        if (result) {
            return ServiceResult.builder().build().asResult();
        }

        return ServiceResult.builder().serviceCode(BaseErrorCode.ERROR).build().asResult();
    }

    @ApiOperation(value = "修改自己的密码", notes = "")
    @PostMapping("/resetpwd/self")
    public ServiceResult resetSelfPwd(@Validated(UpdateGroup.class) @RequestBody ResetSelfPwdRequest req) {
        String decryptedOldPwd = PasswordUtils.desDecrypt(req.getOldPwd());
        String decryptedNewPwd = PasswordUtils.desDecrypt(req.getNewPwd());
        boolean success = getModelService().resetSelfPwd(decryptedOldPwd, decryptedNewPwd);
        if (success) {
            return ServiceResult.builder().build().asResult();
        }

        return ServiceResult.builder().serviceCode(BaseErrorCode.ERROR).build().asResult();
    }


    @ApiOperation(value = "禁用用户", notes = "")
    @PostMapping("/disable")
    public ServiceResult disable(@RequestParam String userId) {
        boolean success = getModelService().disable(userId);
        if (success) {
            return ServiceResult.builder().build().asResult();
        }
        return ServiceResult.builder().serviceCode(BaseErrorCode.ERROR).build().asResult();
    }


    @ApiOperation(value = "启用用户", notes = "")
    @PostMapping("/enable")
    public ServiceResult enable(@RequestParam String userId) {
        boolean success = getModelService().enable(userId);
        if (success) {
            return ServiceResult.builder().build().asResult();
        }
        return ServiceResult.builder().serviceCode(BaseErrorCode.ERROR).build().asResult();
    }


    @ApiOperation(value = "审批用户", notes = "")
    @PostMapping("/approve")
    public ServiceResult approve(@RequestParam String userId) {
        boolean success = getModelService().approve(userId);
        if (success) {
            return ServiceResult.builder().build().asResult();
        }
        return ServiceResult.builder().serviceCode(BaseErrorCode.ERROR).build().asResult();
    }


    @ApiOperation(value = "更改用户头像", notes = "")
    @PostMapping("/updateAvatar")
    public ServiceResult updateAvatar(@RequestParam String userId, MultipartFile file) throws MpRuntimeException {
        boolean success = getModelService().updateAvatar(userId, file);
        if (success) {
            return ServiceResult.builder().build().asResult();
        }
        return ServiceResult.builder().serviceCode(BaseErrorCode.ERROR).build().asResult();
    }


    @PostMapping("/excel/export")
    public ServiceResult exportUsers() {
        // todo excel 导出
        return null;
    }


    @PostMapping("/excel/import")
    public ServiceResult importUsers() {
        // todo excel 导入
        return null;
    }

    @GetMapping("/excel/template")
    public void excelTemplate() {
        getModelService().downloadExcelTemplate();
    }


}
