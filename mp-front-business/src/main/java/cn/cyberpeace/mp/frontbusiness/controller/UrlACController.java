 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UrlACController.java.
 // File size 5027.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.controller;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.request.validate.AddGroup;
import cn.cyberpeace.mp.auth.request.validate.UpdateGroup;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.frontbusiness.dto.UrlAcCtlGroupBo;
import cn.cyberpeace.mp.frontbusiness.dto.UrlAccessCtlDto;
import cn.cyberpeace.mp.frontbusiness.request.urlac.UrlAccessCtlRequest;
import cn.cyberpeace.mp.frontbusiness.service.UrlACService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

 /**
  * url accontroller
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Api(value = "接口访问控制", tags = "接口访问控制")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/urlac")
public class UrlACController {

     /**
      *
      */
     @Autowired
    private UrlACService urlACService;

     /**
      * 模糊url列表
      *
      * @param url url
      * @return {@link ServiceResult }<{@link List }<{@link UrlAccessCtlDto }>>
      */
     @ApiOperation(value = "根据url模糊查询", notes = "")
    @ApiImplicitParam(name = "url", value = "url", dataTypeClass = String.class, required = true, dataType = "query")
    @GetMapping("/by_url")
    public ServiceResult<List<UrlAccessCtlDto>> listByFuzzyUrl(@RequestParam String url) {
        List<UrlAccessCtlDto> urlAccessCtlDtoList = urlACService.listByUrlFuzzyQuery(url);
        return ResultCommon.success(urlAccessCtlDtoList);
    }

     /**
      * 按目标列出
      *
      * @param group 集团
      * @return {@link ServiceResult }<{@link List }<{@link UrlAcCtlGroupBo }>>
      */
     @ApiOperation(value = "根据 group 查询", notes = "")
    @ApiImplicitParam(name = "group", value = "菜单", dataTypeClass = String.class, required = true, dataType = "query")
    @GetMapping("/by_group")
    public ServiceResult<List<UrlAcCtlGroupBo>> listByTarget(@RequestParam String group) {
        return ResultCommon.success(urlACService.listByGroupQuery(group));
    }

     /**
      * 按目标id列表
      *
      * @param ids id
      * @return {@link ServiceResult }<{@link List }<{@link UrlAccessCtlDto }>>
      */
     @ApiOperation(value = "根据权限绑定的url", notes = "")
    @ApiImplicitParam(name = "ids", value = "id列表", dataTypeClass = String[].class, required = true, dataType = "query")
    @GetMapping("/by_targets")
    public ServiceResult<List<UrlAccessCtlDto>> listByTargetIds(@RequestParam String[] ids) {
        List<UrlAccessCtlDto> urlAccessCtlDtoList = urlACService.listByTargetIds(Arrays.asList(ids));
        return ResultCommon.success(urlAccessCtlDtoList);
    }

     /**
      * 添加
      *
      * @param addRequest
      * @return {@link ServiceResult }<{@link String }>
      */
     @ApiOperation(value = "新增URL访问控制", notes = "")
    @ApiImplicitParam(name = "addRequest", value = "请求体", dataTypeClass = UrlAccessCtlRequest.class, required = true, dataType = "body")
    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult<String> add(@RequestBody @Validated(AddGroup.class) UrlAccessCtlRequest addRequest) {
        String urlACId = urlACService.add(addRequest);
        urlACService.evictURLACCache();
        return ResultCommon.success(urlACId);
    }

     /**
      * 修改
      *
      * @param updateRequest
      * @return {@link ServiceResult }<{@link String }>
      */
     @ApiOperation(value = "更新URL访问控制", notes = "")
    @ApiImplicitParam(name = "updateRequest", value = "请求体", dataTypeClass = UrlAccessCtlRequest.class, required = true, dataType = "body")
    @PatchMapping("/modify")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult<String> modify(@RequestBody @Validated(UpdateGroup.class) UrlAccessCtlRequest updateRequest) {
        String urlACId = urlACService.modify(updateRequest);
        urlACService.evictURLACCache();
        return ResultCommon.success(urlACId);
    }

     /**
      * 删除
      *
      * @param ids id
      * @return {@link ServiceResult }<{@link Integer }>
      */
     @ApiOperation(value = "删除URL访问控制", notes = "")
    @ApiImplicitParam(name = "ids", value = "id列表", dataTypeClass = String[].class, required = true, dataType = "query")
    @DeleteMapping
    public ServiceResult<Integer> delete(@RequestParam String[] ids) {
        int cnt = urlACService.delete(ids);
        urlACService.evictURLACCache();
        return ResultCommon.success(cnt);
    }

     /**
      * @return {@link ServiceResult }
      */
     @ApiOperation(value = "删除缓存", notes = "")
    @PostMapping("/evict_cache")
    public ServiceResult evictCache() {
        urlACService.evictURLACCache();
        return ResultCommon.success();
    }

}
