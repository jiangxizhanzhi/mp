package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualSwitchCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualSwitchDeleteData;
import com.fasterxml.jackson.core.type.TypeReference;

public class VirtualSwitch extends CloudClient {

    public VirtualSwitchCreateData create(VirtualSwitchCreateData createData) {
        CloudResponse<VirtualSwitchCreateData> result = getHttpClient().successPost(
                CloudUrlConstant.VIRTUAL_SWITCH_CREATE_URL, createData, null,
                new TypeReference<>() {});
        // TODO 错误码
        return result.getData();
    }

    public boolean delete(VirtualSwitchDeleteData deleteData) {
        CloudResponse<Object> result = getHttpClient().successDelete(CloudUrlConstant.VIRTUAL_SWITCH_DELETE_URL,
                deleteData, null, new TypeReference<CloudResponse<Object>>() {});
        return true;
    }
}
