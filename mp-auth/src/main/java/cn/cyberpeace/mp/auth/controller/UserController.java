package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.AuthLogConstant;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.ResetPwdRequest;
import cn.cyberpeace.mp.auth.dto.ResetSelfPwdRequest;
import cn.cyberpeace.mp.auth.dto.excel.ExportUser;
import cn.cyberpeace.mp.auth.dto.excel.ImportUser;
import cn.cyberpeace.mp.auth.dto.user.UserRequest;
import cn.cyberpeace.mp.auth.dto.validate.AddGroup;
import cn.cyberpeace.mp.auth.dto.validate.UpdateGroup;
import cn.cyberpeace.mp.auth.model.UserModel;
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
import com.alibaba.excel.EasyExcel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@Api(value = "用户管理接口", tags = "UserController")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/users")
public class UserController extends BaseModelController<UserModel, UserQueryParams, UserService> {

    @Autowired
    public UserController(UserService modelService) {
        setModelService(modelService);
    }


    @Override
    @ApiOperation(value = "(弃用，建议用/add) 创建数据", notes = "")
    public ObjServiceResult<UserModel> create(UserModel obj, String[] fields) {
        return super.create(obj, fields);
    }

    @Override
    @ApiOperation(value = "(弃用，建议用/modify)更新指定id数据", notes = "")
    public ObjServiceResult<UserModel> update(String id, UserModel obj, String[] fields) {
        return super.update(id, obj, fields);
    }

    @ApiOperation(value = "创建用户，同时完成组织，角色绑定关系", notes = "")
    @PostMapping("/add")
    public ServiceResult add(@RequestBody UserRequest createRequest) {
        UserModel userModel = getModelService().add(createRequest);
        return ResultUtils.commonResult(true, userModel);
    }

    @ApiOperation(value = "修改用户，同时完成组织，角色绑定关系", notes = "")
    @PostMapping("/modify")
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
    @OperateLog(module = AuthLogConstant.MODULE, type = AuthLogConstant.TYPE_UPDATE, title = "禁用用户")
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



    @ApiOperation(value = "导出用户excel", notes = "")
    @PostMapping("/excel/export")
    public void exportExcel(UserQueryParams queryParams, HttpServletResponse response) throws IOException {

        handleQueryParams("list", queryParams);
        ListObjServiceResult<UserModel> listObjServiceResult = getModelService().list(queryParams).asResult();
        List<UserModel> users = listObjServiceResult.getSource().getList();
        List<ExportUser> exportUserList = getModelService().exportExcel(users);

        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("用户导出列表", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), ExportUser.class).sheet("用户导出列表").doWrite(exportUserList);
    }


    @ApiOperation(value = "通过excel导入用户数据", notes = "")
    @PostMapping("/excel/import")
    @ResponseBody
    public ServiceResult importUsers(MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), ImportUser.class, new UploadDataListener(getModelService())).headRowNumber(2).sheet().doRead();
        return ResultUtils.commonResult(true, null);
    }


    @GetMapping("/excel/template")
    public void downloadExcelTemplate() {
        getModelService().downloadExcelTemplate();
    }


}
