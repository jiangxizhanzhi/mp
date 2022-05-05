package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.situationaware.model.ProbeModel;
import cn.cyberpeace.mp.situationaware.model.dto.collection.CollectionTemplateDto;
import cn.cyberpeace.mp.situationaware.model.dto.collection.CollectionTemplatePageItemDto;
import cn.cyberpeace.mp.situationaware.model.dto.collection.CollectionTemplatePageQueryRequest;
import cn.cyberpeace.mp.situationaware.model.dto.collection.CollectionTemplateRequest;
import cn.cyberpeace.mp.situationaware.model.dto.validate.AddGroup;
import cn.cyberpeace.mp.situationaware.model.dto.validate.UpdateGroup;
import cn.cyberpeace.mp.situationaware.service.CollectionTemplateService;
import cn.cyberpeace.mp.situationaware.utils.ResultUtils;
import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/25 17:08
 */
@RestController
@RequestMapping("/collection/template")
@Api(value = "采集模板管理", tags = "采集模板管理")
public class CollectionTemplateController {

    @Autowired
    private CollectionTemplateService templateService;


    @GetMapping("/page")
    @ApiOperation(value = "分页查询采集模板")
    public ListObjServiceResult<CollectionTemplatePageItemDto> page(CollectionTemplatePageQueryRequest pageQueryRequest) {
        PageInfo<CollectionTemplatePageItemDto> pageInfo = templateService.page(pageQueryRequest);
        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }


    @GetMapping("/{id}")
    @ApiOperation(value = "查询采集模板")
    public ServiceResult<CollectionTemplateDto> getById(@PathVariable String id) {
        List<CollectionTemplateDto> colTempDtos = templateService.listByIds(ArrayUtils.toArray(id));
        if (CollectionUtil.isNotEmpty(colTempDtos)) {
            return ServiceResult.success(colTempDtos.get(0));
        }
        return ServiceResult.successful();
    }

    /**
     *
     * @param templateId
     * @param category
     * @param name
     * @return
     */
    @GetMapping("/probes")
    @ApiOperation(value = "查询采集模板可以导入的自定义探针")
    public ServiceResult<List<ProbeModel>> listCustomProbesForTemplate(@RequestParam String templateId, @RequestParam(required = false) Integer category, @RequestParam(required = false) String name) {
        List<ProbeModel> probes = templateService.listCustomProbesNotInTemplate(templateId, category, name);
        return ServiceResult.success(probes);
    }

    /**
     * 新增采集模板
     *
     * @param addRequest 新增采集模板请求体
     * @return ServiceResult
     */
    @PostMapping
    @ApiOperation(value = "新增采集模板")
    public ServiceResult addTemplate(@RequestBody @Validated(AddGroup.class) CollectionTemplateRequest addRequest) {
        templateService.addTemplate(addRequest);
        return ServiceResult.successful();
    }

    /**
     * 修改采集模板
     *
     * @return ServiceResult<Void>
     */
    @PatchMapping
    @ApiOperation(value = "修改采集模板")
    public ServiceResult<Void> modifyTemplate(@RequestBody @Validated(UpdateGroup.class) CollectionTemplateRequest updateRequest) {
        templateService.modifyTemplate(updateRequest);
        return ServiceResult.successful();
    }


    @DeleteMapping()
    @ApiOperation(value = "删除采集模板")
    public ServiceResult deleteByIds(@RequestParam String[] ids) {
        int cnt = templateService.deleteByIds(ids);
        return ServiceResult.success(cnt);
    }

}
