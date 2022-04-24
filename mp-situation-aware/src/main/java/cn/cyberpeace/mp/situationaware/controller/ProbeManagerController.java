package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.situationaware.service.ProbeService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
//    @PostMapping
//    @ApiOperation(value = "新增探针脚本")
//    public ServiceResult<Void> addProbe(
//        @RequestBody @Valid ProbeRequest probeRequest) {
//        probeService.addProbe(probeRequest);
//        return ServiceResult.successful();
//    }
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
//    @GetMapping
//    @ApiOperation(value = "分页查询探针")
//    public ListObjServiceResult<ProbeResult> queryProbes(ProbeSearchRequest probeSearchRequest) {
//        return probeService.queryProbeListWithPageable(probeSearchRequest).asResult();
//    }
//
//    @GetMapping("/{probeId}")
//    @ApiOperation(value = "查询探针详情")
//    public ServiceResult<ProbeResult> queryProbeByProbeId(@PathVariable("probeId") String probeId) {
//        return new ServiceResult<>(probeService.queryProbeDetail(probeId)).asResult();
//    }
}
