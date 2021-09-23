package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualFirewallCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualFirewallDeleteData;
import com.fasterxml.jackson.core.type.TypeReference;

public class VirtualFirewall extends CloudClient {

    public VirtualFirewallCreateData create(VirtualFirewallCreateData createData) {
        CloudResponse<VirtualFirewallCreateData> result = getHttpClient().successPost(
                CloudUrlConstant.VIRTUAL_FIREWALL_CREATE_URL, createData, null,
                new TypeReference<CloudResponse<VirtualFirewallCreateData>>() {});
        return result.getData();
    }

    public boolean delete(VirtualFirewallDeleteData deleteData) {
        CloudResponse<Object> result = getHttpClient().successDelete(CloudUrlConstant.VIRTUAL_FIREWALL_DELETE_URL,
                deleteData, null, new TypeReference<CloudResponse<Object>>() {});
        return true;
    }
}
