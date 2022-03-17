package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualSwitchCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualSwitchDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VirtualSwitch extends CloudClient {

    public VirtualSwitchCreateData create(VirtualSwitchCreateData createData) {
        heavyConcurrentWait();

        CloudResponse<VirtualSwitchCreateData> result = cloudPost(CloudUrlConstant.VIRTUAL_SWITCH_CREATE_URL, createData,
                null, new TypeReference<>() {}, true);
        return result.getData();
    }

    public boolean delete(VirtualSwitchDeleteData deleteData) {
        heavyConcurrentWait();

        try {

            CloudResponse<Object> result = cloudDelete(CloudUrlConstant.VIRTUAL_SWITCH_DELETE_URL, deleteData, null,
                    new TypeReference<CloudResponse<Object>>() {}, true);
            return true;
        }catch (Exception e) {
            log.error("failed to delete node,", e);
            return true;
        }
    }
}
