package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualSwitchCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualSwitchDeleteData;

public class VirtualSwitch extends CloudClient {

    public void create(VirtualSwitchCreateData createData) {
        getHttpClient().successPost(CloudUrlConstant.VIRTUAL_SWITCH_CREATE_URL, createData, null);
    }

    public void delete(VirtualSwitchDeleteData deleteData) {
        getHttpClient().successDelete(CloudUrlConstant.VIRTUAL_SWITCH_DELETE_URL, deleteData, null);
    }
}
