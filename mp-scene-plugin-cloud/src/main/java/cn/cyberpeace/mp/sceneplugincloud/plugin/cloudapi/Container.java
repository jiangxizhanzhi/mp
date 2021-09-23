package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerDeleteData;
import com.fasterxml.jackson.core.type.TypeReference;

public class Container extends CloudClient {

    public ContainerCreateData create(ContainerCreateData createData) {
        CloudResponse<ContainerCreateData> result = getHttpClient().successPost(
                CloudUrlConstant.CONTAINER_CREATE_URL, createData, null,
                new TypeReference<CloudResponse<ContainerCreateData>>() {});
        return result.getData();
    }

    public boolean delete(ContainerDeleteData deleteData) {
        CloudResponse<Object> result = getHttpClient().successDelete(CloudUrlConstant.CONTAINER_DELETE_URL,
                deleteData, null, new TypeReference<CloudResponse<Object>>() {});
        return true;
    }
}
