package cn.cyberpeace.mp.frontbusiness.controller;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
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
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/23 10:43
 */
@Api(value = "接口访问控制", tags = "接口访问控制")
@RestController
@RequestMapping("/front/auth" + "/urlac")
public class UrlACController {

    @Autowired
    private UrlACService urlACService;


    @ApiOperation(value = "根据url模糊查询", notes = "")
    @ApiImplicitParam(name = "url", value = "url", dataTypeClass = String.class, required = true, dataType = "query")
    @GetMapping("/by_url")
    public ServiceResult<List<UrlAccessCtlDto>> listByFuzzyUrl(@RequestParam String url) {
        List<UrlAccessCtlDto> urlAccessCtlDtoList = urlACService.listByUrlFuzzyQuery(url);
        return ResultCommon.success(urlAccessCtlDtoList);
    }

    @ApiOperation(value = "根据 group 查询", notes = "")
    @ApiImplicitParam(name = "group", value = "菜单", dataTypeClass = String.class, required = true, dataType = "query")
    @GetMapping("/by_group")
    public ServiceResult<List<UrlAcCtlGroupBo>> listByTarget(@RequestParam String group) {
        return ResultCommon.success(urlACService.listByGroupQuery(group));
    }

    @ApiOperation(value = "根据权限绑定的url", notes = "")
    @ApiImplicitParam(name = "ids", value = "id列表", dataTypeClass = String[].class, required = true, dataType = "query")
    @GetMapping("/by_targets")
    public ServiceResult<List<UrlAccessCtlDto>> listByTargetIds(@RequestParam String[] ids) {
        List<UrlAccessCtlDto> urlAccessCtlDtoList = urlACService.listByTargetIds(Arrays.asList(ids));
        return ResultCommon.success(urlAccessCtlDtoList);
    }


    @ApiOperation(value = "新增URL访问控制", notes = "")
    @ApiImplicitParam(name = "addRequest", value = "请求体", dataTypeClass = UrlAccessCtlRequest.class, required = true, dataType = "body")
    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult<String> add(@RequestBody @Validated(AddGroup.class) UrlAccessCtlRequest addRequest) {
        String urlACId = urlACService.add(addRequest);
        urlACService.evictURLACCache();
        return ResultCommon.success(urlACId);
    }


    @ApiOperation(value = "更新URL访问控制", notes = "")
    @ApiImplicitParam(name = "updateRequest", value = "请求体", dataTypeClass = UrlAccessCtlRequest.class, required = true, dataType = "body")
    @PatchMapping("/modify")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult<String> modify(@RequestBody @Validated(UpdateGroup.class) UrlAccessCtlRequest updateRequest) {
        String urlACId = urlACService.modify(updateRequest);
        urlACService.evictURLACCache();
        return ResultCommon.success(urlACId);
    }


    @ApiOperation(value = "删除URL访问控制", notes = "")
    @ApiImplicitParam(name = "ids", value = "id列表", dataTypeClass = String[].class, required = true, dataType = "query")
    @DeleteMapping
    public ServiceResult<Integer> delete(@RequestParam String[] ids) {
        int cnt = urlACService.delete(ids);
        urlACService.evictURLACCache();
        return ResultCommon.success(cnt);
    }



    @ApiOperation(value = "删除缓存", notes = "")
    @PostMapping("/evict_cache")
    public ServiceResult evictCache() {
        urlACService.evictURLACCache();
        return ResultCommon.success();
    }

}
