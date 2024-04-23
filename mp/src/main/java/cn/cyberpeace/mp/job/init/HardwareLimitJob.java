package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.http.HttpParam;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.cscene.service.param.CsceneInstanceStatQueryParams;
import cn.cyberpeace.mp.cscene.util.HardwareLimitUtils;
import cn.cyberpeace.mp.devopscluster.converter.bo.HypervisorBO;
import cn.cyberpeace.mp.job.base.init.MpInitiate;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 启动完后生命周期进行
 */
@Component
@Slf4j
public class HardwareLimitJob {

    @Resource
    private CsceneInstanceMapper csceneInstanceMapper;

    @Resource
    private CsceneSpecialService csceneSpecialService;

    @MpInitiate(description = "cpu、内存、磁盘使用数量的初始化", id = "hardware_limit_init")
    public void exec() {
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

}
