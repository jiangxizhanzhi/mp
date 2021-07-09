package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.AuthLogConstant;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.ResetPwdRequest;
import cn.cyberpeace.mp.auth.dto.ResetSelfPwdRequest;
import cn.cyberpeace.mp.auth.dto.excel.ExportUser;
import cn.cyberpeace.mp.auth.dto.excel.ImportUser;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.dto.user.UserExportRequest;
import cn.cyberpeace.mp.auth.dto.user.UserLogPageRequest;
import cn.cyberpeace.mp.auth.dto.user.UserRequest;
import cn.cyberpeace.mp.auth.dto.validate.AddGroup;
import cn.cyberpeace.mp.auth.dto.validate.UpdateGroup;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.UserLogService;
import cn.cyberpeace.mp.auth.service.UploadDataListener;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.param.UserQueryParams;
import cn.cyberpeace.mp.auth.util.PasswordUtils;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.log.annotation.OperateLog;
import cn.cyberpeace.mp.devops.log.model.OperationLogModel;
import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@Api(value = "用户管理接口", tags = "AUTH-用户管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/users")
public class UserController extends BaseModelController<UserModel, UserQueryParams, UserService> {

    @Autowired
    private UserLogService userLogService;

    @Autowired
    private OrgUserService orgUserService;

    @Autowired
    public UserController(UserService modelService) {
        setModelService(modelService);
    }


    @Override
    @ApiIgnore
    @ApiOperation(value = "(弃用，建议用/api/mp/auth/org_users/page/byorg) 分页查询组织下的用户", notes = "")
    public ListObjServiceResult<UserModel> list(UserQueryParams queryParams) {
        return super.list(queryParams);
    }

    @Override
    @ApiIgnore
    @ApiOperation(value = "(弃用，建议用/add) 创建数据", notes = "")
    public ObjServiceResult<UserModel> create(UserModel obj, String[] fields) {
        return super.create(obj, fields);
    }

    @Override
    @ApiOperation(value = "(弃用，建议用/modify)更新指定id数据", notes = "")
    public ObjServiceResult<UserModel> update(String id, UserModel obj, String[] fields) {
        return super.update(id, obj, fields);
    }

    @Override
    @ApiOperation(value = "查询指定id数据 附带组织，角色信息", notes = "")
    public ObjServiceResult get(@PathVariable String id, @RequestParam(required = false) String[] fields) {
        UserDto userDto = getModelService().getUserDto(id);
        return ResultUtils.obj2ObjServiceResult(userDto);
    }

    @ApiOperation(value = "查询当前登录用户信息 附带组织，角色信息", notes = "")
    @GetMapping("/current")
    public ObjServiceResult getCurrentUser() {
        UserDto userDto = getModelService().getCurrentUserDto();
        return ResultUtils.obj2ObjServiceResult(userDto);
    }


    @ApiOperation(value = "创建用户，同时完成组织，角色绑定关系", notes = "")
    @PostMapping("/add")
    @OperateLog(module = AuthLogConstant.MODULE, type = AuthLogConstant.TYPE_ADD, title = "新增用户")
    public ServiceResult add(@RequestBody UserRequest createRequest) {
        UserModel userModel = getModelService().add(createRequest);
        return ResultUtils.commonResult(true, userModel);
    }

    @ApiOperation(value = "修改用户，同时完成组织，角色绑定关系", notes = "")
    @PostMapping("/modify")
    @OperateLog(module = AuthLogConstant.MODULE, type = AuthLogConstant.TYPE_UPDATE, title = "更新用户")
    public ServiceResult modify(@RequestBody UserRequest modifyRequest) {
        UserModel userModel = getModelService().modify(modifyRequest);
        return ResultUtils.commonResult(true, userModel);
    }


    @ApiOperation(value = "修改密码", notes = "")
    @PostMapping("/resetpwd")
    public ServiceResult resetPwd(@Validated(AddGroup.class) @RequestBody ResetPwdRequest resetPwdRequest) {
        String decryptedPwd = PasswordUtils.desDecrypt(resetPwdRequest.getNewPwd());
        boolean success = getModelService().resetPwd(resetPwdRequest.getUserId(), decryptedPwd);
        return ResultUtils.commonResult(success, null);
    }

    @ApiOperation(value = "修改自己的密码", notes = "")
    @PostMapping("/resetpwd/self")
    public ServiceResult resetSelfPwd(@Validated(UpdateGroup.class) @RequestBody ResetSelfPwdRequest req) {
        String decryptedOldPwd = PasswordUtils.desDecrypt(req.getOldPwd());
        String decryptedNewPwd = PasswordUtils.desDecrypt(req.getNewPwd());
        boolean success = getModelService().resetSelfPwd(decryptedOldPwd, decryptedNewPwd);
        return ResultUtils.commonResult(success, null);
    }


    @ApiOperation(value = "禁用用户", notes = "")
    @PostMapping("/disable")
    public ServiceResult disable(@RequestBody List<String> userIds) {
        String errMsg = getModelService().batchDisable(userIds);
        if (StringUtils.isBlank(errMsg)) {
            return ResultUtils.commonResult(true, null);
        }
//        ModuleEnums mo = ModuleEnums.AUTH.getCode();
//        mo.getCode();
        return ResultUtils.commonResult(true, errMsg);
    }


    @ApiOperation(value = "启用用户", notes = "")
    @PostMapping("/enable")
    public ServiceResult enable(@RequestBody List<String> userIds) {
        String errMsg = getModelService().batchEnable(userIds);
        if (StringUtils.isBlank(errMsg)) {
            return ResultUtils.commonResult(true, null);
        }
        return ResultUtils.commonResult(true, errMsg);
    }


    @ApiOperation(value = "审批用户", notes = "")
    @PostMapping("/approve")
    public ServiceResult approve(@RequestBody List<String> userIds) {
        String errMsg = getModelService().batchApprove(userIds);
        if (StringUtils.isBlank(errMsg)) {
            return ResultUtils.commonResult(true, null);
        }
        return ResultUtils.commonResult(true, errMsg);
    }


    @ApiOperation(value = "更改用户头像", notes = "")
    @PostMapping("/updateAvatar")
    public ServiceResult updateAvatar(@RequestParam String userId, MultipartFile file) throws MpRuntimeException {
        boolean success = getModelService().updateAvatar(userId, file);
        return ResultUtils.commonResult(success, null);
    }

    @ApiOperation(value = "查询用户操作日志", notes = "")
    @GetMapping("/log")
    public ListObjServiceResult userlog(UserLogPageRequest logPageRequest) {
        PageInfo pageInfo = userLogService.getUserLog(logPageRequest);
        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }


}
