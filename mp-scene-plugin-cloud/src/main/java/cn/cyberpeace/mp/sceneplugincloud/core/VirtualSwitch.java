package cn.cyberpeace.mp.sceneplugincloud.core;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.core.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.core.param.VirtualSwitchCreateData;
import cn.cyberpeace.mp.sceneplugincloud.core.param.VirtualSwitchDeleteData;

public class VirtualSwitch extends CloudClient {

    public void create(VirtualSwitchCreateData createData) {
        getHttpClient().successPost(CloudUrlConstant.VIRTUAL_SWITCH_CREATE_URL, createData, null);
    }

    public void delete(VirtualSwitchDeleteData deleteData) {
        getHttpClient().successDelete(CloudUrlConstant.VIRTUAL_SWITCH_DELETE_URL, deleteData, null);
    }
}
