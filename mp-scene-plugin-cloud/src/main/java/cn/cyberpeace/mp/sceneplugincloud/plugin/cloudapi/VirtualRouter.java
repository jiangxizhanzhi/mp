package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualRouterCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualRouterDeleteData;

public class VirtualRouter extends CloudClient {

    public void create(VirtualRouterCreateData createData) {
        getHttpClient().successPost(CloudUrlConstant.VIRTUAL_ROUTER_CREATE_URL, createData, null);
    }

    public void delete(VirtualRouterDeleteData deleteData) {
        getHttpClient().successDelete(CloudUrlConstant.VIRTUAL_ROUTER_DELETE_URL, deleteData, null);
    }
}
