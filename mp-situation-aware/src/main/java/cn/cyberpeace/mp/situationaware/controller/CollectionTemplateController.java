package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.situationaware.model.dto.collection.*;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeDto;
import cn.cyberpeace.mp.situationaware.model.dto.validate.UpdateGroup;
import cn.cyberpeace.mp.situationaware.service.CollectionTemplateService;
import cn.cyberpeace.mp.situationaware.utils.ResultUtils;
import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/collection/template")
@Api(value = "采集模板管理", tags = "采集模板管理")
public class CollectionTemplateController {

    @Autowired
    private CollectionTemplateService templateService;


    @GetMapping("/page")
    @ApiOperation(value = "分页查询采集模板，不含自定义探针如脚本，参数等）")
    public ListObjServiceResult<CollectionTemplatePageItemDto> page(CollectionTemplatePageQueryRequest pageQueryRequest) {
        PageInfo<CollectionTemplatePageItemDto> pageInfo = templateService.page(pageQueryRequest);
        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }


    @GetMapping
    @ApiOperation(value = "查询采集模板，包含自定义探针参数，不含自定义探针脚本")
    @ApiImplicitParam(name = "id", value = "模板ID", dataTypeClass = String.class)
    public ServiceResult<CollectionTemplateDto> getById(@RequestParam String id) {
        List<CollectionTemplateDto> colTempDtos = templateService.listByIds(ArrayUtils.toArray(id));
        if (CollectionUtil.isNotEmpty(colTempDtos)) {
            return ResultCommon.success(colTempDtos.get(0));
        }
        return ResultCommon.success();
    }


    @GetMapping("/probe_details")
    @ApiOperation(value = "查询采集模板中的某个探针详细信息，包括脚本内容")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "templateId", value = "模板ID", dataTypeClass = String.class),
        @ApiImplicitParam(name = "probeId", value = "探针ID", dataTypeClass = String.class)
    })
    public ServiceResult<CollectionTemplateProbeDto> getProbeDetailsById(@RequestParam String templateId, @RequestParam String probeId) {
        CollectionTemplateProbeDto tempProbeDto = templateService.getTemplateProbeDtoWithScriptContent(templateId, probeId);
        return ResultCommon.success(tempProbeDto);
    }

    
    @GetMapping("/probes_to_import")
    @ApiOperation(value = "查询可以导入的自定义探针列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "templateId", value = "模板ID", dataTypeClass = String.class),
            @ApiImplicitParam(name = "category", value = "探针分类", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "name", value = "探针名称", dataTypeClass = Integer.class)
    })
    public ServiceResult<List<ProbeDto>> listCustomProbesForTemplate(@RequestParam(required = false) String templateId,
                                                                     @RequestParam(required = false) Integer category,
                                                                     @RequestParam(required = false) String name) {
        List<ProbeDto> probes = templateService.listCustomProbesForTemplate(templateId, category, name);
        return ResultCommon.success(probes);
    }

    
    @PostMapping
    @ApiOperation(value = "新增采集模板")
    @ApiImplicitParam(name = "addRequest", value = "采集模板新增请求体")
    public ServiceResult addTemplate(@RequestBody CollectionTemplateRequest addRequest) {
        String id = templateService.addTemplate(addRequest);
        return ResultCommon.success(id);
    }

    
    @PatchMapping
    @ApiOperation(value = "修改采集模板")
    @ApiImplicitParam(name = "updateRequest", value = "采集模板修改请求体")
    public ServiceResult<Void> modifyTemplate(@RequestBody @Validated(UpdateGroup.class) CollectionTemplateRequest updateRequest) {
        templateService.modifyTemplate(updateRequest);
        return ResultCommon.success();
    }


    @DeleteMapping()
    @ApiOperation(value = "删除采集模板")
    @ApiImplicitParam(name = "ids", value = "模板ID集合", dataTypeClass = String[].class)
    public ServiceResult deleteByIds(@RequestParam String[] ids) {
        int cnt = templateService.deleteByIds(ids);
        return ResultCommon.success(cnt);
    }

}
