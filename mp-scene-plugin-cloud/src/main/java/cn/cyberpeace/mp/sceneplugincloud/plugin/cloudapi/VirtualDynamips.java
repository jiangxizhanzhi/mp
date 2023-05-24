package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualDynamicCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualDynamicDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class VirtualDynamips extends CloudClient {

    public VirtualDynamicCreateData create(VirtualDynamicCreateData createData) {
        heavyConcurrentWait();
        CloudResponse<VirtualDynamicCreateData> response = cloudPost(CloudUrlConstant.CREATE_DYNAMIPS, createData, null,
                new TypeReference<CloudResponse<VirtualDynamicCreateData>>() {
                }, true);
        return response.getData();
    }

    public void delete(List<String> resourceIds) {
        heavyConcurrentWait();
        VirtualDynamicDeleteData deleteData = new VirtualDynamicDeleteData();
        deleteData.setVms(resourceIds);
        cloudDelete(CloudUrlConstant.DELETE_DYNAMIPS, deleteData, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }

    public void sendRule(String resourceId, List<String> cmds) {

    }
}
