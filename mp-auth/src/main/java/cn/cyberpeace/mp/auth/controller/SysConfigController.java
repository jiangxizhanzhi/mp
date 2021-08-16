package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.SysConfigConstant;
import cn.cyberpeace.mp.auth.model.SysConfigModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.AvatarService;
import cn.cyberpeace.mp.auth.service.sys.SysConfigService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.controller.stereotype.UnlimitPagination;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.hutool.core.collection.CollectionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Api(value = "配置管理接口", tags = "AUTH-配置管理接口")
@Slf4j
@RestController
@RequestMapping("/sys/config")
@UnlimitPagination("list")
public class SysConfigController extends BaseModelController<SysConfigModel, QueryParams, SysConfigService> {

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private UserService userService;

    @Autowired
    public SysConfigController(SysConfigService modelService) {
        setModelService(modelService);
    }

    @ApiOperation(value = "查询所有系统设置", notes = "")
    @GetMapping("/all")
    public ServiceResult getall() {
        List<SysConfigModel> configs = getModelService().listAll();
        return ResultUtils.commonResult(true, configs);
    }

    @ApiOperation(value = "根据keyname更新单条sysconfig", notes = "")
    @PostMapping("/update/bykeyname")
    public ServiceResult updateByKeyName(@RequestBody SysConfigModel updateConfig) {

        if (updateConfig.getKeyName().equals(SysConfigConstant.KEY_USER_AUDIT) && updateConfig.getValue().equals("0")) {
            // 如果要关闭审核功能，需要检查用户中有没有处于待审核状态的
            List<UserModel> list = userService.listNotAudited();
            if (CollectionUtil.isNotEmpty(list)) {
//                throw new MpRuntimeException(AuthErrorCode.SYS_CONFIG_CANNOT_CLOSE_AUDIT);
                return ServiceResult.builder().serviceCode(AuthErrorCode.SYS_CONFIG_CANNOT_CLOSE_AUDIT).build().asResult();
            }
        }

        boolean success = getModelService().updateByKeyName(updateConfig);
        return ResultUtils.commonResult(success, null);
    }

    @ApiOperation(value = "根据keyname批量更新sysconfig", notes = "")
    @PostMapping("/updateall")
    public ServiceResult updateAll(@RequestBody List<SysConfigModel> configs) {
        // 更新规则，如果key存在，就修改value值，如果key不存在，就忽略不改
        boolean success = getModelService().batchUpdateByKeyname(configs);
        return ResultUtils.commonResult(success, null);
    }


    @ApiOperation(value = "系统配置 初始化", notes = "")
    @PostMapping("/init")
    public ServiceResult init() {
        // 更新规则，如果key存在，就修改value值，如果key不存在，就忽略不改
        boolean success = avatarService.initDefaultAvatars();
        return ResultUtils.commonResult(success, null);
    }


    @Override
    @ApiIgnore
    public ObjServiceResult<SysConfigModel> create(SysConfigModel obj, String[] fields) {
        throw new MpRuntimeException("Not Supported!");
    }

    @Override
    @ApiIgnore
    public ServiceResult<Integer> bulkCreate(List<SysConfigModel> objs) {
        throw new MpRuntimeException("Not Supported!");
    }

    @Override
    @ApiIgnore
    public ObjServiceResult<SysConfigModel> update(String id, SysConfigModel obj, String[] fields) {
        throw new MpRuntimeException("Not Supported!");
    }

    @Override
    @ApiIgnore
    public ServiceResult<Integer> bulkUpdate(List<SysConfigModel> objs) {
        throw new MpRuntimeException("Not Supported!");
    }

    @Override
    @ApiIgnore
    public ServiceResult<Boolean> delete(String id) {
        throw new MpRuntimeException("Not Supported!");
    }

    @Override
    @ApiIgnore
    public ServiceResult<Integer> bulkDelete(String[] ids) {
        throw new MpRuntimeException("Not Supported!");
    }


}
