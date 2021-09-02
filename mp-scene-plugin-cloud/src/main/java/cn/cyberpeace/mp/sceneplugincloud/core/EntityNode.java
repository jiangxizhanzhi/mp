package cn.cyberpeace.mp.sceneplugincloud.core;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.core.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.core.param.EntityNodeCreateData;
import cn.cyberpeace.mp.sceneplugincloud.core.param.EntityNodeDeleteData;

public class EntityNode extends CloudClient {

    public void create(EntityNodeCreateData createData) {
        getHttpClient().successPost(CloudUrlConstant.ENTITY_NODE_CREATE_URL, createData, null);
    }

    public void delete(EntityNodeDeleteData deleteData) {
        getHttpClient().successDelete(CloudUrlConstant.ENTITY_NODE_DELETE_URL, deleteData, null);
    }
}
