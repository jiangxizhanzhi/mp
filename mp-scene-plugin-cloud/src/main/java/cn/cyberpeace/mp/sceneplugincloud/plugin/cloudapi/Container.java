package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;

public class Container extends CloudClient {

    public ContainerCreateData create(ContainerCreateData createData) {
        CloudResponse<ContainerCreateData> result = post(CloudUrlConstant.CONTAINER_CREATE_URL, createData, null,
                new TypeReference<CloudResponse<ContainerCreateData>>() {}, true);
        return result.getData();
    }

    public boolean delete(ContainerDeleteData deleteData) {
        CloudResponse<Object> result = delete(CloudUrlConstant.CONTAINER_DELETE_URL, deleteData, null,
                new TypeReference<CloudResponse<Object>>() {}, true);
        return true;
    }
}
