package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.GetPhysicalSwitchPortConnectionParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.PhysicalSwitchPortConnectionCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.PhysicalSwitchPortConnectionDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudListData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class PhysicalSwitch extends CloudClient {
    /**
     * getPortConnections
     *
     * @param name
     * @return
     */
    public CloudListData<List<String>> getPortConnections(String name) {
        GetPhysicalSwitchPortConnectionParam param = new GetPhysicalSwitchPortConnectionParam();
        param.setL1SwitchName(name);
        CloudResponse<CloudListData<List<String>>> result = cloudGet(CloudUrlConstant.GET_PHYSICAL_SWITCH_PORT_CONNECTION,
                param, new TypeReference<CloudResponse<CloudListData<List<String>>>>() {
                }, true);
        return result.getData();
    }

    /**
     * createPortConnection
     *
     * @param data
     */
    public void createPortConnection(PhysicalSwitchPortConnectionCreateData data) {
        cloudPost(CloudUrlConstant.ADD_PHYSICAL_SWITCH_PORT_CONNECTION, data, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }

    /**
     * deletePortConnection
     *
     * @param data
     */
    public void deletePortConnection(PhysicalSwitchPortConnectionDeleteData data) {
        cloudDelete(CloudUrlConstant.DELETE_PHYSICAL_SWITCH_PORT_CONNECTION, data, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }

    /**
     * getPorts
     *
     * @param name
     * @return
     */
    public CloudListData<String> getPorts(String name) {
        GetPhysicalSwitchPortConnectionParam param = new GetPhysicalSwitchPortConnectionParam();
        param.setL1SwitchName(name);
        CloudResponse<CloudListData<String>> result = cloudGet(CloudUrlConstant.GET_PHYSICAL_PORT_URL,
                param, new TypeReference<CloudResponse<CloudListData<String>>>() {
                }, true);
        return result.getData();
    }

}
