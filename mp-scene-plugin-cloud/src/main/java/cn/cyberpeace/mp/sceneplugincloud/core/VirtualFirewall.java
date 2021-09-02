package cn.cyberpeace.mp.sceneplugincloud.core;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.core.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.core.param.VirtualFirewallCreateData;
import cn.cyberpeace.mp.sceneplugincloud.core.param.VirtualFirewallDeleteData;

public class VirtualFirewall extends CloudClient {

    public void create(VirtualFirewallCreateData createData) {
        getHttpClient().successPost(CloudUrlConstant.VIRTUAL_FIREWALL_CREATE_URL, createData, null);
    }

    public void delete(VirtualFirewallDeleteData deleteData) {
        getHttpClient().successDelete(CloudUrlConstant.VIRTUAL_FIREWALL_DELETE_URL, deleteData, null);
    }
}
