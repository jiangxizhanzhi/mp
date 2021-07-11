package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.bo.OptionBO;
import cn.cyberpeace.mp.common.service.OptionService;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeAbilityDto;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeDto;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbePageQueryRequest;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeRequest;
import cn.cyberpeace.mp.situationaware.model.dto.validate.AddGroup;
import cn.cyberpeace.mp.situationaware.model.dto.validate.UpdateGroup;
import cn.cyberpeace.mp.situationaware.service.ProbeAbilityService;
import cn.cyberpeace.mp.situationaware.service.ProbeScriptService;
import cn.cyberpeace.mp.situationaware.service.ProbeService;
import cn.cyberpeace.mp.situationaware.utils.ResultUtils;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/probes")
@Api(value = "探针管理", tags = "探针管理")
public class ProbeManagerController {

    @Autowired
    private ProbeService probeService;

    @Autowired
    private ProbeAbilityService probeAbilityService;

    @Autowired
    private ProbeScriptService probeScriptService;

    @Resource(name = "sysOptionServiceImpl")
    private OptionService optionService;

    @GetMapping("/types")
    @ApiOperation(value = "分页查询探针类型")
    public ServiceResult<List<OptionBO>> probeTypes() {
        return optionService.getIntOptions(SituationalAwareConstant.MP_COMMON_OPTION_TYPE_SITUATION_PROBE_TYPE);
    }

    @GetMapping("/os-types")
    @ApiOperation(value = "查询操作系统类型")
    public ServiceResult<List<OptionBO>> osTypes() {
        return optionService.getIntOptions(SituationalAwareConstant.MP_COMMON_OPTION_TYPE_SITUATION_OS_TYPE);
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页查询探针，不含探针脚本内容")
    public ListObjServiceResult<ProbeDto> page(ProbePageQueryRequest pageQueryRequest) {
        log.info("query probe meta request param: {}" ,pageQueryRequest);
        PageInfo<ProbeDto> pageInfo = probeService.page(pageQueryRequest);
        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }

    @GetMapping("/abilities/page")
    @ApiOperation(value = "分页查询探针能力")
    public ListObjServiceResult<ProbeAbilityDto> pageAbilities(ProbePageQueryRequest pageQueryRequest) {
        PageInfo<ProbeAbilityDto> pageInfo = probeAbilityService.page(pageQueryRequest);
        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }


    @GetMapping
    @ApiOperation(value = "根据id查询单个探针详情，包含探针脚本内容")
    @ApiImplicitParam(name = "id", value = "探针ID", dataTypeClass = String.class)
    public ServiceResult<ProbeDto> getById(@RequestParam String id) {
        List<ProbeDto> probeDtos = probeService.listByIds(Arrays.asList(id));
        if (CollectionUtil.isNotEmpty(probeDtos)) {
            return ResultCommon.success(probeDtos.get(0));
        }
        return ResultCommon.success();
    }

    @GetMapping("/batch")
    @ApiOperation(value = "根据id数组批量查询探针详情，包含探针脚本内容")
    @ApiImplicitParam(name = "ids", value = "探针ID集合", dataTypeClass = String[].class)
    public ServiceResult<List<ProbeDto>> getByIds(@RequestParam String[] ids) {

        List<ProbeDto> probeDtos = probeService.listByIds(Arrays.asList(ids));
        return ResultCommon.success(probeDtos);
    }

    
    @PostMapping
    @ApiOperation(value = "新增自定义探针")
    @OperateLog(module = OpModuleEnums.PROBE, type = OpTypeEnums.CREATE, title = "创建探针", target = OpTargetEnums.PROBE)
    @ApiImplicitParam(name = "probeRequest", value = "新增请求", dataTypeClass = ProbeRequest.class)
    public ServiceResult addProbe(@RequestBody @Validated(AddGroup.class) ProbeRequest probeRequest) {
        if (StringUtils.isBlank(probeRequest.getScriptUrl()) && StringUtils.isNotBlank(probeRequest.getScriptContent())) {
            String httpUrl = probeScriptService.uploadScriptContentToFDFS(probeRequest.getScriptContent(), probeRequest.getScriptType());
            String md5 = DigestUtil.md5Hex(probeRequest.getScriptContent());
            probeRequest.setScriptChecksum(md5);
            probeRequest.setScriptUrl(httpUrl);
        }
        probeService.addProbe(probeRequest);
        return ResultCommon.success();
    }


    @PatchMapping
    @ApiOperation(value = "修改自定义探针")
    @OperateLog(module = OpModuleEnums.PROBE, type = OpTypeEnums.UPDATE, title = "修改探针", target = OpTargetEnums.PROBE)
    @ApiImplicitParam(name = "probeRequest", value = "修改请求", dataTypeClass = ProbeRequest.class)
    public ServiceResult modifyProbe(@RequestBody @Validated(UpdateGroup.class) ProbeRequest probeRequest) {
        var oldScript = probeScriptService.getScriptModelByProbeId(probeRequest.getId());
        String oldMd5 = oldScript.getChecksum();
        Integer oldScriptType = oldScript.getScriptType();
        
        boolean contentChanged = false;
        if (Objects.nonNull(probeRequest.getScriptType()) && !oldScriptType.equals(probeRequest.getScriptType())) {
            contentChanged = true;
        }
        if (StringUtils.isNotBlank(probeRequest.getScriptContent())) {
            String newMd5 = DigestUtil.md5Hex(probeRequest.getScriptContent());

            if (!Objects.equals(newMd5, oldMd5) || contentChanged) {
                String httpUrl = probeScriptService.uploadScriptContentToFDFS(probeRequest.getScriptContent(), probeRequest.getScriptType());
                probeRequest.setScriptChecksum(newMd5);
                probeRequest.setScriptUrl(httpUrl);
                contentChanged = true;
            }
        }

        if (!contentChanged) {
            probeRequest.setScriptContent(null);
            probeRequest.setScriptUrl(null);
            probeRequest.setScriptChecksum(null);
        }

        probeService.modifyProbe(probeRequest);
        return ResultCommon.success();
    }


    @DeleteMapping()
    @ApiOperation(value = "删除探针")
    @ApiImplicitParam(name = "ids", value = "探针ID集合", dataTypeClass = String[].class)
    @OperateLog(module = OpModuleEnums.PROBE, type = OpTypeEnums.DELETE, title = "删除探针", target = OpTargetEnums.PROBE)
    public ServiceResult deleteByIds(@RequestParam String[] ids) {

        int cnt = probeService.deleteByIds(Arrays.asList(ids));
        return ResultCommon.success(cnt);
    }

    @ApiOperation(value = "导入脚本文件，返回脚本内容", notes = "")
    @ApiImplicitParam(name = "file", value = "脚本文件", dataTypeClass = MultipartFile.class)
    @PostMapping("/script/import")
    @ResponseBody
    public ServiceResult<String> upload(MultipartFile file) {
        String content = probeScriptService.readScriptFileContent(file);
        return ResultCommon.success(content);
    }
}
