package cn.cyberpeace.mp.datasandtable.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.datasandtable.constant.SituationalAwareErrorCode;
import cn.cyberpeace.mp.datasandtable.model.ProbeRequest;
import cn.cyberpeace.mp.datasandtable.model.ProbeResult;
import cn.cyberpeace.mp.datasandtable.model.ProbeSearchRequest;
import cn.cyberpeace.mp.datasandtable.service.ProbeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

    @DeleteMapping("/{probeId}")
    @ApiOperation(value = "探针脚本删除")
    public ServiceResult deleteProbeById(@PathVariable("probeId") String probeId) {
        return probeService.deleteProbeById(probeId) ? ServiceResult.successful(): ServiceResult.fail(SituationalAwareErrorCode.PROBE_DELETE_FAIL,"probe delete fail");
    }

    @GetMapping
    @ApiOperation(value = "分页查询探针")
    public ListObjServiceResult<ProbeResult> queryProbes(ProbeSearchRequest probeSearchRequest) {
        return probeService.queryProbeListWithPageable(probeSearchRequest);
    }

    @GetMapping("/{probeId}")
    @ApiOperation(value = "查询探针详情")
    public ServiceResult<ProbeResult> queryProbeByProbeId(@PathVariable("probeId") String probeId) {
        return ServiceResult.<ProbeResult>builder().data(probeService.queryProbeDetail(probeId)).build();
    }
}
