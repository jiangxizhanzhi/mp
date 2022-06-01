package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.EntityNodeCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.EntityNodeDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;

public class EntityNode extends CloudClient {

    public EntityNodeCreateData create(EntityNodeCreateData createData) {
        heavyConcurrentWait();

        CloudResponse<EntityNodeCreateData> result = cloudPost(CloudUrlConstant.ENTITY_NODE_CREATE_URL, createData, null,
                new TypeReference<CloudResponse<EntityNodeCreateData>>() {}, true);
        return result.getData();
    }

    public boolean delete(EntityNodeDeleteData deleteData) {
        heavyConcurrentWait();

        try {
            CloudResponse<Object> result = cloudDelete(CloudUrlConstant.ENTITY_NODE_DELETE_URL, deleteData, null,
                    new TypeReference<CloudResponse<Object>>() {}, true);
        } catch (Exception e) {
            if (super.excludeDeleteException(e)) {
                return true;
            }
            throw e;
        }
        return true;
    }
}
