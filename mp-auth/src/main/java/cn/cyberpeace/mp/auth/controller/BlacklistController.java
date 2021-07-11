package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.BlacklistModel;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


@Api(value = "访问黑名单开关", tags = "AUTH-访问黑名单开关")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/blacklist")
public class BlacklistController {
    @Resource
    private BlacklistModel blacklistModel;

    @ApiOperation("更新访问黑名单开关")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flag", value = "启用开关", dataTypeClass = Boolean.class),
    })
    @OperateLog(module = OpModuleEnums.SYSTEM, type = OpTypeEnums.UPDATE, title = "更新黑名单开关", target = OpTargetEnums.SYS_CONFIG)
    @PutMapping("/enabled/{flag}")
    
    public ObjServiceResult<Boolean> setBlacklistFlag(@PathVariable(value = "flag") boolean flag) {
        blacklistModel.setEnabled(flag);
        return new ObjServiceResult<>(true).asResult();
    }

    @ApiOperation("查看访问黑名单开关")
    @GetMapping("/enabled")
    public ObjServiceResult<Boolean> getBlacklistFlag() {
        return new ObjServiceResult<>(blacklistModel.isEnabled()).asResult();
    }
}
