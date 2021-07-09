package cn.cyberpeace.mp.auth.controller;


import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.excel.ExportUser;
import cn.cyberpeace.mp.auth.dto.excel.ImportUser;
import cn.cyberpeace.mp.auth.dto.user.UserExportRequest;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.UploadDataListener;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.alibaba.excel.EasyExcel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@Api(value = "用户导入导出Excel管理接口", tags = "AUTH-用户导入导出Excel接口")
@Slf4j
@RequestMapping(ControllerConstant.BASE_API_PATH + "/users")
public class UserExcelController {

    @Autowired
    private OrgUserService orgUserService;

    @Autowired
    private UserService userService;

    @ApiOperation(value = "导出用户数据excel", notes = "")
    @PostMapping("/excel/export")
    public void exportExcel(HttpServletResponse response, @RequestBody UserExportRequest exportRequest) throws IOException {

        List<UserModel> users =  orgUserService.listUsersByOrgAndUname(exportRequest.getOrg(), exportRequest.getUsername());
        List<ExportUser> exportUserList = userService.exportExcel(users);

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
        EasyExcel.read(file.getInputStream(), ImportUser.class, new UploadDataListener(userService)).headRowNumber(2).sheet().doRead();
        return ResultUtils.commonResult(true, null);
    }


    @ApiOperation(value = "下载用户导入excel模板", notes = "")
    @GetMapping("/excel/template")
    public void downloadExcelTemplate() {
        userService.downloadExcelTemplate();
    }

}
