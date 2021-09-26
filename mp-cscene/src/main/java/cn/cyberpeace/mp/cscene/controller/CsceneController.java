package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.controller.BaseModelListControllerBase;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceCreateContextBO;
import cn.cyberpeace.mp.cscene.bo.CsceneStatBO;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.cscene.service.param.CsceneQueryParams;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "场景模板", tags = "CSCENE-场景模板")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/cscenes")
@Data
public class CsceneController implements BaseModelListControllerBase {

    private CsceneService modelService;

    private CsceneSpecialService csceneSpecialService;

    @Autowired
    public CsceneController(CsceneService csceneService, CsceneSpecialService csceneSpecialService) {
        setModelService(csceneService);
        setCsceneSpecialService(csceneSpecialService);
    }

    @ApiOperation("获取分页数据")
    @GetMapping
    public ListObjServiceResult<CsceneBO> list(CsceneQueryParams queryParams) {
        handleQueryParams("list", queryParams);
        return getModelService().listBO(queryParams, false, null).asResult();
    }

    @ApiOperation("获取指定id场景模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class),
    })
    @GetMapping(params = "id")
    public ServiceResult<CsceneBO> get(@RequestParam String id) {
        return modelService.getById(id, false, null).asResult();
    }

    @ApiOperation("导出指定id场景模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class),
    })
    @GetMapping(value = "/export", params = "id")
    public ServiceResult<CsceneBO> export(@RequestParam String id) {
        return modelService.export(id, null).asResult();
    }

    @ApiOperation(value = "保存（创建/更新）场景模板", notes="请忽略test_scenes和app_scenes字段")
    @PostMapping("/sync")
    public ServiceResult<CsceneBO> sync(@RequestBody CsceneBO csceneBO) {
        return modelService.sync(csceneBO, null).asResult();
    }

    @ApiOperation("克隆指定id场景模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class),
    })
    @GetMapping(value = "/clone", params = "id")
    public ServiceResult<CsceneBO> clone(@RequestParam String id) {
        return modelService.clone(id, null).asResult();
    }

    @ApiOperation("删除场景模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class),
    })
    @DeleteMapping(params = "id")
    public ServiceResult<Boolean> delete(@RequestParam String id) {
        return modelService.deleteById(id, false, null).asResult();
    }

    @ApiOperation("批量删除场景模板")
    @DeleteMapping("/bulk_delete")
    public ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        return modelService.bulkDeleteByIds(ids, false, null).asResult();
    }

    @ApiOperation("创建场景模板测试实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class),
    })
    @PostMapping(value = "/test_instance", params = "id")
    public ServiceResult<CsceneInstanceBO> createTestInstance(
            @RequestParam String id, @RequestBody(required = false) CsceneInstanceCreateContextBO createContext) {
        return modelService.createTestInstance(id, false, createContext).asResult();
    }

    @ApiOperation("创建场景模板应用实例")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "场景模板id", dataTypeClass = String.class),
    })
    @PostMapping(value = "/app_instance", params = "id")
    public ServiceResult<CsceneInstanceBO> createAppInstance(
            @RequestParam String id, @RequestBody(required = false) CsceneInstanceCreateContextBO createContext) {
        return modelService.createAppInstance(id, false, createContext).asResult();
    }

    @ApiOperation("获取场景模板统计数据")
    @GetMapping("/stat")
    public ServiceResult<List<CsceneStatBO>> stat(@RequestParam String[] ids) {
        return csceneSpecialService.stat(ids, null).asResult();
    }

}
