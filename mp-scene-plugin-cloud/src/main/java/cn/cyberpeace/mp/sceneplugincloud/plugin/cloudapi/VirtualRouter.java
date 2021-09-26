package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualRouterCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualRouterDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;

public class VirtualRouter extends CloudClient {

    public VirtualRouterCreateData create(VirtualRouterCreateData createData) {
        CloudResponse<VirtualRouterCreateData> result = getHttpClient().successPost(
                CloudUrlConstant.VIRTUAL_ROUTER_CREATE_URL, createData, null,
                new TypeReference<CloudResponse<VirtualRouterCreateData>>() {});
        return result.getData();
    }

    public boolean delete(VirtualRouterDeleteData deleteData) {
        CloudResponse<Object> result = getHttpClient().successDelete(CloudUrlConstant.VIRTUAL_ROUTER_DELETE_URL,
                deleteData, null, new TypeReference<CloudResponse<Object>>() {});
        return true;
    }
}
