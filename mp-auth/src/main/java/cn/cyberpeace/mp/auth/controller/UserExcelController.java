package cn.cyberpeace.mp.auth.controller;


import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.AuthBasei18nCode;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.bo.excel.ExportUserBo;
import cn.cyberpeace.mp.auth.bo.excel.ExportUserEnBo;
import cn.cyberpeace.mp.auth.bo.excel.ImportUserBo;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.request.user.UserExportRequest;
import cn.cyberpeace.mp.auth.service.*;
import cn.cyberpeace.mp.auth.util.ExcelCheck;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
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
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


@Api(value = "用户导入导出Excel管理接口", tags = "AUTH-用户导入导出Excel接口")
@Slf4j
@Controller
@RequestMapping(ControllerConstant.BASE_API_PATH + "/users")
public class UserExcelController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private OperateUserPermissionService opUserPermCheckService;

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private TenantUserService tenantUserService;


    @ApiOperation(value = "导出用户数据excel", notes = "")
    @ApiImplicitParam(name = "exportRequest", value = "导出请求", dataTypeClass = UserExportRequest.class)
    @PostMapping("/excel/export")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public void exportExcel(HttpServletResponse response, @RequestBody UserExportRequest exportRequest) throws IOException {
        String[] ids = exportRequest.getIds();
        List<String> userIds = null;
        if (Objects.nonNull(ids) && ids.length > 0) {
            userIds = CollectionUtil.toList(ids);
        }

        List<UserModel> users = tenantUserService.listUsersByOrgAndUname(exportRequest.getOrg(), exportRequest.getUsername(), userIds);

        Locale locale = LocaleContextHolder.getLocale();
        String sheetName = MessageCommon.get(AuthBasei18nCode.EXCEL_EXPORT_NAME);
        String fname = MessageCommon.get(AuthBasei18nCode.EXCEL_EXPORT_NAME);
        List dataLst = null;
        Class exportClz = null;

        if (Objects.equals(locale, Locale.US)) {
            dataLst = userService.exportExcelEn(users);
            exportClz = ExportUserEnBo.class;
        } else {
            dataLst = userService.exportExcel(users);
            exportClz = ExportUserBo.class;
        }

        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode(fname, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), exportClz).sheet(sheetName).doWrite(dataLst);
    }


    @ApiOperation(value = "通过excel导入用户数据", notes = "")
    @ApiImplicitParam(name="file", value="文件", dataTypeClass = MultipartFile.class)
    @PostMapping("/excel/import")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    @ResponseBody
    public ServiceResult importUsers(MultipartFile file) throws IOException {
        ParamCheck.notNull(file, AuthBasei18nCode.REQUEST_PARAMS);
        ExcelCheck.check(file.getInputStream(), file.getOriginalFilename());
        try {
            EasyExcel.read(file.getInputStream(), ImportUserBo.class, new UploadDataListener(userService, roleService, orgService, opUserPermCheckService, authConfig))
                    .headRowNumber(2).sheet().doRead();
        } catch (ExcelCommonException | ExcelAnalysisException | ExcelDataConvertException | ExcelGenerateException ex) {
            log.error("ex:{}", ExceptionUtil.stacktraceToString(ex));
            throw new MpRuntimeException(AuthErrorCode.EXCEL_FORMAT_ERROR);
        }
        return ResultCommon.success();
    }


    @ApiOperation(value = "下载用户导入excel模板", notes = "")
    @GetMapping("/excel/template")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public void downloadExcelTemplate() {
        userService.downloadExcelTemplate();
    }

}
