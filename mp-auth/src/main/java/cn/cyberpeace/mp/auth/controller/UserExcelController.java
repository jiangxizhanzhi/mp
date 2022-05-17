package cn.cyberpeace.mp.auth.controller;


import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.excel.ExportUser;
import cn.cyberpeace.mp.auth.dto.excel.ExportUserEn;
import cn.cyberpeace.mp.auth.dto.excel.ImportUser;
import cn.cyberpeace.mp.auth.dto.user.UserExportRequest;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.OperateUserPermissionService;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.auth.service.UploadDataListener;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.ExcelCheck;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.exception.ExcelCommonException;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.exception.ExcelGenerateException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
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
import java.util.Locale;
import java.util.Objects;

/**
 * @Description: 用户导入导出Excel管理接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "用户导入导出Excel管理接口", tags = "AUTH-用户导入导出Excel接口")
@Slf4j
@Controller
@RequestMapping(ControllerConstant.BASE_API_PATH + "/users")
public class UserExcelController {

    @Autowired
    private OrgUserService orgUserService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private OperateUserPermissionService opUserPermCheckService;


    @ApiOperation(value = "导出用户数据excel", notes = "")
    @PostMapping("/excel/export")
    public void exportExcel(HttpServletResponse response, @RequestBody UserExportRequest exportRequest) throws IOException {
        String[] ids = exportRequest.getIds();
        List<String> userIds = null;
        if (Objects.nonNull(ids) && ids.length > 0) {
            userIds = CollectionUtil.toList(ids);
        }

        List<UserModel> users = orgUserService.listUsersByOrgAndUname(exportRequest.getOrg(), exportRequest.getUsername(), userIds);

        Locale locale = LocaleContextHolder.getLocale();
        String sheetName = MessageCommon.get(AuthBasei18nCode.EXCEL_EXPORT_NAME);
        String fname = MessageCommon.get(AuthBasei18nCode.EXCEL_EXPORT_NAME);
        List dataLst = null;
        Class exportClz = null;

        if (Objects.equals(locale, Locale.US)) {
            dataLst = userService.exportExcelEn(users);
            exportClz = ExportUserEn.class;
        } else {
            dataLst = userService.exportExcel(users);
            exportClz = ExportUser.class;
        }

        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode(fname, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), exportClz).sheet(sheetName).doWrite(dataLst);
    }


    @ApiOperation(value = "通过excel导入用户数据", notes = "")
    @PostMapping("/excel/import")
    @ResponseBody
    public ServiceResult importUsers(MultipartFile file) throws IOException {
        ExcelCheck.check(file);
        try {
            EasyExcel.read(file.getInputStream(), ImportUser.class, new UploadDataListener(userService, roleService, orgService, opUserPermCheckService)).headRowNumber(2).sheet().doRead();
        } catch (ExcelCommonException | ExcelAnalysisException | ExcelDataConvertException | ExcelGenerateException ex) {
            log.error("ex:{}", ExceptionUtil.stacktraceToString(ex));
            throw new MpRuntimeException(AuthErrorCode.EXCEL_FORMAT_ERROR);
        }
        return ServiceResult.successful();
    }


    @ApiOperation(value = "下载用户导入excel模板", notes = "")
    @GetMapping("/excel/template")
    public void downloadExcelTemplate() {
        userService.downloadExcelTemplate();
    }

}
