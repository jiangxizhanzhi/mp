package cn.cyberpeace.mp.frontadmin.controller;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.frontadmin.service.sys.activmenu.ActivMenuService;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/16 19:02
 */
@Api(value = "SYS-ADMIN", tags = "ADMIN")
@Slf4j
@RestController
@RequestMapping("/sys/admin")
public class ActivMenuController {

    @Autowired
    private ActivMenuService activMenuService;

    @PostMapping("/menu/reimport")
    @OperateLog(module = OpModuleEnums.SYSTEM, type = OpTypeEnums.UPDATE, title = "更新菜单列表", target = OpTargetEnums.MENU)
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult activMenu(MultipartFile file) {
        activMenuService.reImportMenus(file);
        return ResultCommon.success();
    }

}
