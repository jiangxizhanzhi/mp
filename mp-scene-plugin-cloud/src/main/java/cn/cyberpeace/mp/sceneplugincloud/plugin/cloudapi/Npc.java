package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NpcCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NpcCreateResult;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;

public class Npc extends CloudClient {

    public Npc() {
        super();
    }

    public NpcCreateResult create(NpcCreateData createData) {
        heavyConcurrentWait();

        CloudResponse<Object> result = cloudPost(CloudUrlConstant.CREATE_NPC_URL, createData, null,
                new TypeReference<CloudResponse<Object>>() {
                }, true);
        // todo
        return null;
    }

    public boolean delete(NpcDeleteData deleteData) {
        heavyConcurrentWait();

        try {
            CloudResponse<Object> result = cloudDelete(CloudUrlConstant.DELETE_NPC_URL, deleteData, null,
                    new TypeReference<CloudResponse<Object>>() {
                    }, true);
            return true;
        } catch (Exception e) {
            if (super.excludeDeleteException(e)) {
                return true;
            }
            throw e;
        }
    }
}
