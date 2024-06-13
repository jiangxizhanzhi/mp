package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.http.HttpParam;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.service.param.CsceneInstanceStatQueryParams;
import cn.cyberpeace.mp.cscene.util.HardwareLimitUtils;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class HardwareLimitRefreshService {
    @Resource
    private CsceneInstanceMapper csceneInstanceMapper;

    @Resource
    private CsceneSpecialService csceneSpecialService;

    public void init() {
        this.initUsed();
        this.initTotal();
    }

    private void initUsed() {
        List<CsceneInstanceModel> instances = csceneInstanceMapper.list(new HumanCondition().filterEqual(CsceneInstanceModel::getDeleted, false));
        List<String> ids = new ArrayList<>();
        for (CsceneInstanceModel model : instances) {
            ids.add(model.getId());
        }
        CsceneInstanceStatQueryParams params = new CsceneInstanceStatQueryParams();
        params.setIds(ids.toArray(new String[]{}));
        var stats = csceneSpecialService.listCsceneInstanceStats(params).getSource().getObjs();
        int cpu = 0, ram = 0, disk = 0;
        for (var stat : stats) {
            cpu += stat.getCpu();
            ram += stat.getRam();
            disk += stat.getDisk();
        }
        HardwareLimitUtils.initUsed(cpu, ram, disk);
    }

    private void initTotal() {
        PageParam param = new PageParam(1, 100);
        CloudResponse<HypervisorResponse> response = new Remote().cloudGet(CloudUrlConstant.HYPERVISORS_STATISTICS, param, new TypeReference<CloudResponse<HypervisorResponse>>() {
        }, true);
        List<HypervisorBO> statistics = response.getData().getList();
        int cpu = 0, ram = 0, disk = 0;
        for (var stat : statistics) {
            cpu += stat.getCpu();
            ram += stat.getRam();
            disk += stat.getDisk();
        }
        HardwareLimitUtils.initTotal(cpu, ram, disk);
    }

    @Getter
    @Setter
    private static class HypervisorResponse {
        private List<HypervisorBO> list;

        private Integer total;
    }

    @Getter
    @Setter
    private static class PageParam implements HttpParam {
        private Integer limit;

        private Integer offset;

        public PageParam(Integer offset, Integer limit) {
            this.limit = limit;
            this.offset = offset;
        }
    }


    @Getter
    @Setter
    private static class HypervisorBO {
        private String name;
        private String ip;
        private Integer cpu;
        private Long ram;
        private Long disk;
        private Integer cpuUsed;
        private Long ramUsed;
        private Long diskUsed;
        private BigDecimal cpuPercent;
        private BigDecimal ramPercent;
        private BigDecimal diskPercent;
        private Integer totalInstances;
        private Integer totalContainers;
        private Integer weight;
        private Integer maxInstances;
        private boolean serviceStatus;
    }
}
