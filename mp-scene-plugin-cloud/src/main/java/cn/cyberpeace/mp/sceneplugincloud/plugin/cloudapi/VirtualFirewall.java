package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualFirewallCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualFirewallDeleteData;

public class VirtualFirewall extends CloudClient {

    public void create(VirtualFirewallCreateData createData) {
        getHttpClient().successPost(CloudUrlConstant.VIRTUAL_FIREWALL_CREATE_URL, createData, null);
    }

    public void delete(VirtualFirewallDeleteData deleteData) {
        getHttpClient().successDelete(CloudUrlConstant.VIRTUAL_FIREWALL_DELETE_URL, deleteData, null);
    }
}
