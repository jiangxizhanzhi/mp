package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.code.MessageServiceCode;
import cn.cyberpeace.mp.base.util.http.HttpParam;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
public class HardwareLimitUtils {

    public static void check(CsceneInstanceBO instance) {
        HardwareCount count = getHardwareCount(instance.getScene().getTopology().getElements());
        int ram = count.getRam();
        int disk = count.getDisk();

        PageParam param = new PageParam(1, 100);
        CloudResponse<HypervisorResponse> response = new Remote().cloudGet(CloudUrlConstant.HYPERVISORS_STATISTICS, param, new TypeReference<CloudResponse<HypervisorResponse>>() {
        }, true);
        List<HypervisorBO> statistics = response.getData().getList();
        int totalRam = 0, totalDisk = 0;
        int usedRam = 0, usedDisk = 0;
        int freeRam = 0, freeDisk = 0;

        for (var stat : statistics) {
            if ("up".equals(stat.getState()) && "enabled".equals(stat.getStatus())) {
                totalRam += stat.getRam();
                totalDisk += stat.getDisk();

                usedDisk += stat.getDiskUsed();
                usedRam += stat.getRamUsed();
            }
        }

        freeRam = totalRam - usedRam;
        freeDisk = totalDisk - usedDisk;

//        // cpu可以超分 暂时不校验
//        if (ram > freeRam) {
//            throw new MpRuntimeException(CsceneErrorCode.RAM_LIMIT.format(String.valueOf(totalRam), String.valueOf(usedRam), String.valueOf(ram)));
//        }
//        if (disk > freeDisk) {
//            throw new MpRuntimeException(CsceneErrorCode.DISK_LIMIT.format(String.valueOf(totalDisk), String.valueOf(usedDisk), String.valueOf(disk)));
//        }
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
        private String state;
        private String status;
    }

    private static HardwareCount getHardwareCount(List<SceneElementBO> elements) {
        int cpu = 0;
        int disk = 0;
        int ram = 0;
        for (SceneElementBO e : elements) {
            int eCpu = Optional.ofNullable(e.getCpu()).orElse(0);
            int eRam = Optional.ofNullable(e.getRam()).orElse(0);
            int eDisk = Optional.ofNullable(e.getDisk()).orElse(0);
            cpu += eCpu;
            disk += eDisk;
            ram += eRam;
        }

        HardwareCount count = new HardwareCount();
        count.setDisk(disk);
        count.setCpu(cpu);
        count.setRam(ram);
        return count;
    }

    @Getter
    @Setter
    public static class UpdateResult {
        private boolean success;
        private int limitNode;
        private int currentNode;
        private int existNode;
        private CsceneErrorCode errorCode;

        public MessageServiceCode getMessage() {
            return errorCode.format(String.valueOf(limitNode), String.valueOf(existNode), String.valueOf(currentNode));
        }
    }

    @Getter
    @Setter
    private static class HardwareCount {
        private int cpu;
        private int disk;
        private int ram;
    }
}