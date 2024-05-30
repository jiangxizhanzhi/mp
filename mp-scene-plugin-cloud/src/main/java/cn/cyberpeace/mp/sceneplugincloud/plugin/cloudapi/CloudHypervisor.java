package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.http.SnakeHttpParam;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.HypervisorData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CloudHypervisor extends CloudClient {
    private static List<HypervisorData> cache;

    public static void refreshCachedHypervisors() {
        cache = new CloudHypervisor().getCachedHypervisors();
    }

    public List<HypervisorData> getCachedHypervisors() {
        if (cache == null) {
            cache = this.getHypervisors();
        }
        return cache;
    }

    public List<HypervisorData> getHypervisors() {
        var params = new HypervisorCloudPageParams();
        CloudResponse<HypervisorCloudData> response = cloudGet(CloudUrlConstant.HYPERVISORS_STATISTICS, params, new TypeReference<CloudResponse<HypervisorCloudData>>() {
        }, true);
        if (response != null && response.getData() != null && CollectionUtils.isNotEmpty(response.getData()
                                                                                                 .getList())) {
            return response.getData().getList();
        } else {
            log.error("get hypervisors empty , params is {} , response is {}", JsonCommon.stringify(params), JsonCommon.stringify(response));
            return new ArrayList<>();
        }
    }

    @Getter
    @Setter
    private static class HypervisorCloudPageParams implements SnakeHttpParam {
        private int limit = 999;
        private int offset = 1;
    }

    @Getter
    @Setter
    private static class HypervisorCloudData {
        private List<HypervisorData> list;
        private Integer total;
    }
}
