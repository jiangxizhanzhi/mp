 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BlacklistController.java.
 // File size 1930.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
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

 /**
  * @author litian
  * @date 2023年09月25 13:54
  */
 @Api(value = "访问黑名单开关", tags = "AUTH-访问黑名单开关")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/blacklist")
public class BlacklistController {
     /**
      * 黑名单模式
      */
     @Resource
    private BlacklistModel blacklistModel;

     /**
      * 设置黑名单标志
      *
      * @param flag 国旗
      * @return {@link ObjServiceResult }<{@link Boolean }>
      */
     @ApiOperation("更新访问黑名单开关")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flag", value = "启用开关", dataTypeClass = Boolean.class),
    })
    @OperateLog(module = OpModuleEnums.SYSTEM, type = OpTypeEnums.UPDATE, title = "更新黑名单开关", target = OpTargetEnums.SYS_CONFIG)
    @PutMapping("/enabled/{flag}")
    // @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ObjServiceResult<Boolean> setBlacklistFlag(@PathVariable(value = "flag") boolean flag) {
        blacklistModel.setEnabled(flag);
        return new ObjServiceResult<>(true).asResult();
    }

     /**
      * 获取黑名单标志
      *
      * @return {@link ObjServiceResult }<{@link Boolean }>
      */
     @ApiOperation("查看访问黑名单开关")
    @GetMapping("/enabled")
    public ObjServiceResult<Boolean> getBlacklistFlag() {
        return new ObjServiceResult<>(blacklistModel.isEnabled()).asResult();
    }
}
