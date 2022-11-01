package cn.cyberpeace.mp.flow.v2.client.cloud;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/24
 */
public class VirtualMachine {
    private CloudHttpClient client = new CloudHttpClient("http://10.100.7.1:11111", 6000, 10);

    public AddPortResult addPort(String instanceId, String networkId) {
        AddPortData data = new AddPortData();
        data.setNetworkId(networkId);
        String url = CloudUrlConstant.ADD_VM_PORTS.replace("{instance_id}", instanceId);
        var result = client.cloudPost(url, data, null, new TypeReference<CloudResponse<AddPortResult>>() {
        });
        return result.getData();
    }
}