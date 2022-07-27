package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.log.annotation.OperateLog;
import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import cn.cyberpeace.mp.situationaware.model.OsTypeModel;
import cn.cyberpeace.mp.situationaware.model.ProbeTypeModel;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeAbilityDto;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeDto;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbePageQueryRequest;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeRequest;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbeTypeVO;
import cn.cyberpeace.mp.situationaware.model.dto.validate.AddGroup;
import cn.cyberpeace.mp.situationaware.model.dto.validate.UpdateGroup;
import cn.cyberpeace.mp.situationaware.service.*;
import cn.cyberpeace.mp.situationaware.utils.ResultUtils;
import cn.cyberpeace.mp.situationaware.vo.OsTypeVO;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

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

    @Autowired
    private OsTypeService osTypeService;

    @Autowired
    private ProbeTypeService probeTypeService;

    @GetMapping("/types")
    @ApiOperation(value = "分页查询探针类型")
    public ServiceResult<ProbeTypeVO> probeTypes() {
        List<ProbeTypeVO> typeVOs = new ArrayList<>();
        List<ProbeTypeModel> models = probeTypeService.findAll();
        Locale locale = LocaleContextHolder.getLocale();
        for (ProbeTypeModel model : models) {
            String name = null;
            if (Objects.equals(locale, Locale.US)) {
                name = model.getEnName();
            } else {
                name = model.getChName();
            }
            int code = model.getCode();
            typeVOs.add(
                    ProbeTypeVO.builder()
                            .name(name)
                            .code(code)
                            .build()
            );
        }
        return ServiceResult.success(typeVOs);
    }

    @GetMapping("/os-types")
    @ApiOperation(value = "查询操作系统类型")
    public ServiceResult<OsTypeVO> osTypes() {
        List<OsTypeVO> osTypeVOs = new ArrayList<>();
        List<OsTypeModel> models = osTypeService.findAll();
        Locale locale = LocaleContextHolder.getLocale();
        for (OsTypeModel model : models) {
            String name = null;
            if (Objects.equals(locale, Locale.US)) {
                name = model.getEnName();
            } else {
                name = model.getChName();
            }
            int code = model.getCode();
            osTypeVOs.add(
                    OsTypeVO.builder()
                            .name(name)
                            .code(code)
                            .build()
            );
        }
        return ServiceResult.success(osTypeVOs);
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页查询探针，不含探针脚本内容")
    public ListObjServiceResult<ProbeDto> page(ProbePageQueryRequest pageQueryRequest) {
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
    public ServiceResult<ProbeDto> getById(@RequestParam String id) {
        List<ProbeDto> probeDtos = probeService.listByIds(Arrays.asList(id));
        if (CollectionUtil.isNotEmpty(probeDtos)) {
            return ServiceResult.success(probeDtos.get(0));
        }
        return ServiceResult.successful();
    }

    @GetMapping("/batch")
    @ApiOperation(value = "根据id数组批量查询探针详情，包含探针脚本内容")
    public ServiceResult<List<ProbeDto>> getByIds(@RequestParam String[] ids) {

        List<ProbeDto> probeDtos = probeService.listByIds(Arrays.asList(ids));
        return ServiceResult.success(probeDtos);
    }

    /**
     * 新增探针
     *
     * @param probeRequest 新增探针参数
     * @return ServiceResult<Void>
     */
    @PostMapping
    @ApiOperation(value = "新增自定义探针")
    @OperateLog(module = OpModuleEnums.PROBE, type = OpTypeEnums.CREATE, title = "创建探针", target = OpTargetEnums.PROBE)
    public ServiceResult addProbe(@RequestBody @Validated(AddGroup.class) ProbeRequest probeRequest) {
        if (StringUtils.isBlank(probeRequest.getScriptUrl()) && StringUtils.isNotBlank(probeRequest.getScriptContent())) {
            String httpUrl = probeScriptService.uploadScriptContentToFDFS(probeRequest.getScriptContent(), probeRequest.getScriptType());
            String md5 = DigestUtil.md5Hex(probeRequest.getScriptContent());
            probeRequest.setScriptChecksum(md5);
            probeRequest.setScriptUrl(httpUrl);
        }
        probeService.addProbe(probeRequest);
        return ServiceResult.successful();
    }


    @PatchMapping
    @ApiOperation(value = "修改自定义探针")
    @OperateLog(module = OpModuleEnums.PROBE, type = OpTypeEnums.UPDATE, title = "修改探针", target = OpTargetEnums.PROBE)
    public ServiceResult modifyProbe(@RequestBody @Validated(UpdateGroup.class) ProbeRequest probeRequest) {
        String oldMd5 = probeScriptService.getScriptChecksumByProbeId(probeRequest.getId());
        // 判断脚本内容是否有变动，如无变动则不需要重新上传脚本
        boolean contentChanged = false;
        if (StringUtils.isNotBlank(probeRequest.getScriptContent())) {
            String newMd5 = DigestUtil.md5Hex(probeRequest.getScriptContent());

            if (!Objects.equals(newMd5, oldMd5)) {
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
        return ServiceResult.successful();
    }


    @DeleteMapping()
    @ApiOperation(value = "删除探针")
    @OperateLog(module = OpModuleEnums.PROBE, type = OpTypeEnums.DELETE, title = "删除探针", target = OpTargetEnums.PROBE)
    public ServiceResult deleteByIds(@RequestParam String[] ids) {

        int cnt = probeService.deleteByIds(Arrays.asList(ids));
        return ServiceResult.success(cnt);
    }

    @ApiOperation(value = "导入脚本文件，返回脚本内容", notes = "")
    @PostMapping("/script/import")
    @ResponseBody
    public ServiceResult<String> upload(MultipartFile file) {
        String content = probeScriptService.readScriptFileContent(file);
        return ServiceResult.success(content);
    }
}
