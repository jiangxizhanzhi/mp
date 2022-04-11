package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualFirewallCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualFirewallDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;

public class VirtualFirewall extends CloudClient {

    public VirtualFirewallCreateData create(VirtualFirewallCreateData createData) {
        heavyConcurrentWait();

        CloudResponse<VirtualFirewallCreateData> result = cloudPost(CloudUrlConstant.VIRTUAL_FIREWALL_CREATE_URL, createData,
                null, new TypeReference<CloudResponse<VirtualFirewallCreateData>>() {
                }, true);
        return result.getData();
    }

    public boolean delete(VirtualFirewallDeleteData deleteData) {
        heavyConcurrentWait();

        try {
            CloudResponse<Object> result = cloudDelete(CloudUrlConstant.VIRTUAL_FIREWALL_DELETE_URL, deleteData, null,
                    new TypeReference<CloudResponse<Object>>() {
                    }, true);
            return true;
        } catch (Exception e) {
            if (super.excludeDeleteException(e)) {
                return true;
            }
            throw e;
        }
    }
}
