package cn.cyberpeace.mp.front.business.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.validate.AddGroup;
import cn.cyberpeace.mp.auth.dto.validate.UpdateGroup;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.front.business.dto.UrlAccessCtlDto;
import cn.cyberpeace.mp.front.business.dto.UrlAccessCtlRequest;
import cn.cyberpeace.mp.front.business.service.UrlACService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
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
    @GetMapping("/by_urls")
    public ServiceResult<List<UrlAccessCtlDto>> listByFuzzyUrls(@RequestParam String[] urls) {
        List<UrlAccessCtlDto> urlAccessCtlDtoList = urlACService.listByUrlFuzzyQuery(Arrays.asList(urls));
        return ServiceResult.success(urlAccessCtlDtoList);
    }

    @ApiOperation(value = "根据权限绑定的url", notes = "")
    @GetMapping("/by_targets")
    public ServiceResult<List<UrlAccessCtlDto>> listByTargetIds(@RequestParam String[] ids) {
        List<UrlAccessCtlDto> urlAccessCtlDtoList = urlACService.listByTargetIds(Arrays.asList(ids));
        return ServiceResult.success(urlAccessCtlDtoList);
    }


    @ApiOperation(value = "新增URL访问控制", notes = "")
    @PostMapping("/add")
    public ServiceResult add(@RequestBody @Validated(AddGroup.class) UrlAccessCtlRequest addRequest) {
        String urlACId = urlACService.add(addRequest);
        urlACService.evictURLACCache();
        urlACService.evictURLACPatternCache();
        return ServiceResult.success(urlACId);
    }


    @ApiOperation(value = "更新URL访问控制", notes = "")
    @PatchMapping("/modify")
    public ServiceResult modify(@RequestBody @Validated(UpdateGroup.class) UrlAccessCtlRequest updateRequest) {
        String urlACId = urlACService.modify(updateRequest);
        urlACService.evictURLACCache();
        urlACService.evictURLACPatternCache();
        return ServiceResult.success(urlACId);
    }


    @ApiOperation(value = "删除URL访问控制", notes = "")
    @DeleteMapping
    public ServiceResult delete(@RequestParam String[] ids) {
        int cnt = urlACService.delete(ids);
        urlACService.evictURLACCache();
        urlACService.evictURLACPatternCache();
        return ServiceResult.success(cnt);
    }



    @ApiOperation(value = "删除缓存", notes = "")
    @PostMapping("/evict_cache")
    public ServiceResult evictCache() {
        urlACService.evictURLACCache();
        urlACService.evictURLACPatternCache();
        return ServiceResult.successful();
    }

}
