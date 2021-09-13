package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(value = "综合场景", tags = "CSCENE-综合场景")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/cscenes")
@Data
public class CsceneController {

    private CsceneService csceneService;

    @Autowired
    public CsceneController(CsceneService csceneService) {
        setCsceneService(csceneService);
    }

    @ApiOperation("获取指定id综合场景")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "综合场景id", dataTypeClass = String.class),
    })
    @GetMapping(params = "id")
    public ServiceResult<CsceneBO> get(@RequestParam String id) {
        return csceneService.getById(id).asResult();
    }

    @ApiOperation(value = "保存（创建/更新）综合场景", notes="请忽略test_scenes和app_scenes字段")
    @PostMapping("/sync")
    public ServiceResult<CsceneBO> sync(@RequestBody CsceneBO csceneBO) {
        return csceneService.sync(csceneBO).asResult();
    }

    @ApiOperation("创建综合场景测试实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "综合场景id", dataTypeClass = String.class),
    })
    @PostMapping(value = "/test_instance", params = "id")
    public ServiceResult<CsceneInstanceBO> createTestInstance(@RequestParam String id) {
        return csceneService.createTestInstance(id, false).asResult();
    }

    @ApiOperation("创建综合场景应用实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "综合场景id", dataTypeClass = String.class),
    })
    @PostMapping(value = "/app_instance", params = "id")
    public ServiceResult<CsceneInstanceBO> createAppInstance(@RequestParam String id) {
        return csceneService.createAppInstance(id, true).asResult();
    }

    @ApiOperation("删除综合场景")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "综合场景id", dataTypeClass = String.class),
    })
    @DeleteMapping(params = "id")
    public ServiceResult<Boolean> delete(@RequestParam String id) {
        return csceneService.delete(id).asResult();
    }

    @ApiOperation("批量删除综合场景")
    @DeleteMapping("/bulk_delete")
    public ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        return csceneService.bulkDelete(ids).asResult();
    }

}
