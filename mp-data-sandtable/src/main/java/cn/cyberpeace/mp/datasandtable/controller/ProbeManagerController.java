package cn.cyberpeace.mp.datasandtable.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.datasandtable.model.ProbeRequest;
import cn.cyberpeace.mp.datasandtable.service.ProbeService;
import io.swagger.annotations.Api;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    @PostMapping
    public ServiceResult<Void> addProbe(
        @RequestBody @Valid ProbeRequest probeRequest) {
        probeService.addProbe(probeRequest);
        return ServiceResult.successful();
    }

    @DeleteMapping("/{probeId}")
    public ServiceResult<Void> deleteProbeById(@PathVariable("probeId") String probeId) {
        probeService.deleteProbeById(probeId);
    }
}
