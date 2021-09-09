package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import cn.cyberpeace.mp.situationaware.model.ProbeRequest;
import cn.cyberpeace.mp.situationaware.model.ProbeResult;
import cn.cyberpeace.mp.situationaware.model.ProbeSearchRequest;
import cn.cyberpeace.mp.situationaware.service.ProbeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/probes")
@Api(value = "态势感知-探针管理", tags = "态势感知-探针管理")
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
    @ApiOperation(value = "新增探针脚本")
    public ServiceResult<Void> addProbe(
        @RequestBody @Valid ProbeRequest probeRequest) {
        probeService.addProbe(probeRequest);
        return ServiceResult.successful();
    }

    @DeleteMapping("")
    @ApiOperation(value = "探针脚本删除")
    public ServiceResult deleteProbeById(@RequestBody List<String> probeIds) {
        return probeService.deleteProbeById(probeIds) ? ServiceResult.successful(): ServiceResult.fail(SituationalAwareErrorCode.PROBE_DELETE_FAIL,"probe delete fail");
    }

    @PutMapping
    @ApiOperation(value = "修改探针脚本")
    public ServiceResult modifyProbe(@RequestBody @Valid ProbeRequest probeRequest){
        if (Objects.isNull(probeRequest.getProbeId())) {
            throw new MpRuntimeException(SituationalAwareErrorCode.PROBE_MODIFY_PARAM_ERROR);
        }
        boolean updateSuccess = probeService.modifyProbe(probeRequest) > 0;
        return updateSuccess ? ServiceResult.successful(): ServiceResult.fail(SituationalAwareErrorCode.PROBE_MODIFY_FAIL,"probe update fail");
    }

    @GetMapping
    @ApiOperation(value = "分页查询探针")
    public ListObjServiceResult<ProbeResult> queryProbes(ProbeSearchRequest probeSearchRequest) {
        return probeService.queryProbeListWithPageable(probeSearchRequest).asResult();
    }

    @GetMapping("/{probeId}")
    @ApiOperation(value = "查询探针详情")
    public ServiceResult<ProbeResult> queryProbeByProbeId(@PathVariable("probeId") String probeId) {
        return new ServiceResult<>(probeService.queryProbeDetail(probeId)).asResult();
    }
}
