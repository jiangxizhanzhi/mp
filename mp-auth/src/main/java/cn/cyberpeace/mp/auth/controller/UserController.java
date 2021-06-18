package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.ResetPwdReq;
import cn.cyberpeace.mp.auth.dto.validate.AddGroup;
import cn.cyberpeace.mp.auth.dto.validate.UpdateGroup;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.hutool.core.lang.Assert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "用户管理接口", tags = "UserController")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/users")
public class UserController extends BaseModelController<UserModel, UserService> {

    @Autowired
    public UserController(UserService modelService) {
        setModelService(modelService);
    }


    @ApiOperation(value = "修改密码", notes = "")
    @PostMapping("/resetpwd")
    public ServiceResult resetPwd(@Validated(AddGroup.class) @RequestBody ResetPwdReq resetPwdReq) {
        String decryptedPwd = PasswordUtils.desDecrypt(resetPwdReq.getNewPwd());
        boolean result = getModelService().resetPwd(resetPwdReq.getUserId(), decryptedPwd);
        if(result) {
            return ServiceResult.builder().build().asResult();
        }

        return ServiceResult.builder().serviceCode(BaseErrorCode.ERROR).build().asResult();
    }

    @ApiOperation(value = "修改自己的密码", notes = "")
    @PostMapping("/resetpwd/self")
    public ServiceResult resetSelfPwd(@Validated(UpdateGroup.class) @RequestBody ResetPwdReq resetPwdReq) {
        String decryptedPwd = PasswordUtils.desDecrypt(resetPwdReq.getNewPwd());
        boolean result = getModelService().resetSelfPwd(decryptedPwd);
        if(result) {
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
    public ServiceResult excelTemplate() {
        // todo excel 模板
        return null;
    }





}
