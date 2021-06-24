package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.model.HypervisorOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.model.HypervisorStatisticsOpenStackModel;
import cn.cyberpeace.mp.devops.cluster.response.HypervisorPageResponse;
import cn.cyberpeace.mp.devops.cluster.response.HypervisorStatisticsResponse;
import cn.cyberpeace.mp.devops.cluster.service.HypervisorService;
import cn.cyberpeace.mp.devops.cluster.service.fields.HypervisorFields;
import cn.cyberpeace.mp.devops.cluster.service.fields.HypervisorStatisticsFields;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 容器管理类
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/hypervisor")
public class HypervisorController {

    private HypervisorService hypervisorService;

    @Autowired
    public HypervisorController(HypervisorService hypervisorService) {
        this.hypervisorService = hypervisorService;
    }

    @ApiOperation("资源统计")
    @PostMapping("/statistics")
    public ObjServiceResult statistics(){
        HypervisorStatisticsOpenStackModel model = hypervisorService.statistics();
        HypervisorStatisticsOpenStackModel.HypervisorStatistics hypervisorStatistics = model.getHypervisor_statistics();
        if (Objects.isNull(hypervisorStatistics)){
            return new ObjServiceResult();
        }

        HypervisorStatisticsResponse response = HypervisorStatisticsResponse.builder().vcpus(hypervisorStatistics.getVcpus())
                .memoryMb(hypervisorStatistics.getMemory_mb()).localGb(hypervisorStatistics.getLocal_gb())
                .vcpusUsed(hypervisorStatistics.getVcpus_used()).memoryMbUsed(hypervisorStatistics.getMemory_mb_used())
                .localGbUsed(hypervisorStatistics.getLocal_gb_used()).freeRamMb(hypervisorStatistics.getFree_ram_mb())
                .freeDiskGb(hypervisorStatistics.getFree_disk_gb()).build();

        return new ObjServiceResult(new ObjResult(response, HypervisorStatisticsFields.allFields)).asResult();
    }

    @ApiOperation("节点管理")
    @PostMapping("/page")
    public ObjServiceResult page(){
        HypervisorOpenStackModel model = hypervisorService.page();
        List<HypervisorOpenStackModel.Hypervisor> hypervisorList = model.getHypervisors();
        if (CollectionUtils.isEmpty(hypervisorList)){
            return new ObjServiceResult();
        }

        List<HypervisorPageResponse> list = hypervisorList.stream().map(hypervisor ->
                HypervisorPageResponse.builder().hypervisorHostname(hypervisor.getHostname())
                        .hostIp(hypervisor.getHost_ip()).status(hypervisor.getStatus())
                        .vcpus_percent(hypervisor.getCpu_used()).ram_percent(hypervisor.getRam_used())
                        .disk_percent(hypervisor.getDisk_used()).build()
        ).collect(Collectors.toList());


        return new ObjServiceResult(new ObjResult(list, HypervisorFields.allFields)).asResult();
    }



}
