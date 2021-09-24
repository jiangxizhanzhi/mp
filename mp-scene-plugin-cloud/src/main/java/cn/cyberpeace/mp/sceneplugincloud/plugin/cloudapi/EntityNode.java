package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudResponse;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.EntityNodeCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.EntityNodeDeleteData;
import com.fasterxml.jackson.core.type.TypeReference;

public class EntityNode extends CloudClient {

    public EntityNodeCreateData create(EntityNodeCreateData createData) {
        CloudResponse<EntityNodeCreateData> result = getHttpClient().successPost(
                CloudUrlConstant.ENTITY_NODE_CREATE_URL, createData, null,
                new TypeReference<CloudResponse<EntityNodeCreateData>>() {});
        return result.getData();
    }

    public boolean delete(EntityNodeDeleteData deleteData) {
        CloudResponse<Object> result = getHttpClient().successDelete(CloudUrlConstant.ENTITY_NODE_DELETE_URL,
                deleteData, null, new TypeReference<CloudResponse<Object>>() {});
        return true;
    }
}
