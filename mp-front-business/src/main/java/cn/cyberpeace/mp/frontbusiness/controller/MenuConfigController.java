package cn.cyberpeace.mp.frontbusiness.controller;

import cn.cyberpeace.mp.auth.bo.target.TargetBo;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.service.MenuConfigService;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description: 权限管理接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "菜单管理接口(配置)", tags = "AUTH-权限管理接口(配置)")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/targets_config")
public class MenuConfigController {

    @Resource
    private TargetService targetService;

    @Resource
    private MenuConfigService menuConfigService;

    @ApiOperation(value = "查询当前用户的菜单")
    @GetMapping("/current/tree")
    public ServiceResult<List<TargetBo>> getTree() {
        // 根据当前用户的第一个角色,从模板表里查询菜单
        List<TargetBo> tree = menuConfigService.getTree();
        return new ServiceResult<>(tree).asResult();
    }

    @ApiOperation(value = "导出菜单模板")
    @GetMapping("/tree/export")
    public void exportTree(HttpServletResponse response, @RequestParam(required = false) String templateId) {
        // 根据当前用户的第一个角色,从模板表里查询菜单
        List<TargetBo> tree = menuConfigService.getTree();
        // 写入到文件

    }

    @ApiOperation(value = "导入并覆盖默认菜单模板")
    @GetMapping("/tree/import")
    public void exportTree(MultipartFile file) {

    }

    /**
     * @param actives
     * @return
     */
    @ApiOperation(value = "根据激活配置查询所有菜单")
    @GetMapping("/tree")
    public ServiceResult getTree(List<String> actives) {
        List<TargetBo> tree = targetService.getTree(null, null);
        // 根据传进来的激活项筛选一下
        return new ServiceResult<>(tree).asResult();
    }

    @ApiOperation(value = "覆盖初始模板")
    @PostMapping("/reset_default_menu_template")
    public ServiceResult resetDefaultMenuTemplate(@RequestBody MenuConfigService.MenuTemplate menuTemplate) {
        return new ServiceResult();
    }

    @ApiOperation(value = "新增一套模板")
    @PostMapping("/add_menu_template")
    public ServiceResult addMenuTemplate(@RequestBody MenuConfigService.MenuTemplate menuTemplate) {
        return new ServiceResult();
    }

    @ApiOperation(value = "修改角色和模板的关联关系")
    @PostMapping("/update_menu_template_role")
    public ServiceResult updateRoleTemplate(@RequestBody MenuConfigService.RoleTemplate roleTemplate) {
        return new ServiceResult();
    }

}
