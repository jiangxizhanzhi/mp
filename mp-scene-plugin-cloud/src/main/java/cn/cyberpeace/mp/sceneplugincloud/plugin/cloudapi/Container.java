package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerDeleteData;

public class Container extends CloudClient {

    public void create(ContainerCreateData createData) {
        getHttpClient().successPost(CloudUrlConstant.CONTAINER_CREATE_URL, createData, null);
    }

    public void delete(ContainerDeleteData deleteData) {
        getHttpClient().successDelete(CloudUrlConstant.CONTAINER_DELETE_URL, deleteData, null);
    }
}
