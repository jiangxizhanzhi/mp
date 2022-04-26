package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.situationaware.model.dto.ProbeDto;
import cn.cyberpeace.mp.situationaware.model.dto.ProbePageQueryRequest;
import cn.cyberpeace.mp.situationaware.model.dto.ProbeRequest;
import cn.cyberpeace.mp.situationaware.service.ProbeService;
import cn.cyberpeace.mp.situationaware.utils.ResultUtils;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/probes")
@Api(value = "探针管理", tags = "探针管理")
public class ProbeManagerController {

    @Autowired
    private ProbeService probeService;

    /**
     * 新增探针
     *
     * @param probeRequest 新增探针参数
     * @return ServiceResult<Void>
     */
    @PostMapping
    @ApiOperation(value = "新增自定义探针")
    public ServiceResult<Void> addProbe(@RequestBody @Validated ProbeRequest probeRequest) {
        if (StringUtils.isBlank(probeRequest.getScriptUrl())) {
            String httpUrl = probeService.uploadScriptContent(probeRequest.getScriptContent());
            probeRequest.setScriptUrl(httpUrl);
        }
        probeService.addProbe(probeRequest);
        return ServiceResult.successful();
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页查询探针，不含探针脚本内容")
    public ListObjServiceResult<ProbeDto> page(ProbePageQueryRequest pageQueryRequest) {
        PageInfo<ProbeDto> pageInfo = probeService.page(pageQueryRequest);

//        return new ListObjServiceResult<ProbeDto>(new ListObjResult(pageInfo.getList(), Long.valueOf(pageInfo.getTotal()).intValue()));
        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }
//
//    @DeleteMapping("")
//    @ApiOperation(value = "探针脚本删除")
//    public ServiceResult deleteProbeById(@RequestBody List<String> probeIds) {
//        return probeService.deleteProbeById(probeIds) ? ServiceResult.successful(): ServiceResult.fail(SituationalAwareErrorCode.PROBE_DELETE_FAIL);
//    }
//
//    @PatchMapping("/{probeId}")
//    @ApiOperation(value = "修改探针脚本")
//    public ServiceResult modifyProbe(@RequestBody ProbeRequest probeRequest, @PathVariable("probeId") String probeId){
//        probeRequest.setProbeId(probeId);
//        boolean updateSuccess = probeService.modifyProbe(probeRequest) > 0;
//        return updateSuccess ? ServiceResult.successful(): ServiceResult.fail(SituationalAwareErrorCode.PROBE_MODIFY_FAIL);
//    }
//

//
    @GetMapping("/{id}")
    @ApiOperation(value = "查询探针详情，包含探针脚本内容")
    public ServiceResult<ProbeDto> queryProbeByProbeId(@PathVariable("id") String id) {

        ProbeDto probeDto = probeService.getById(id);
        return ServiceResult.success(probeDto);
    }
}
