package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NpcCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NpcCreateResult;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpClient;

public class Npc extends CloudClient {

    public Npc() {
        super();
//        super.setHttpClient(new CloudHttpClient("http://10.4.1.3:9999", 30000, 10));
    }

    public NpcCreateResult create(NpcCreateData createData) {
        heavyConcurrentWait();

        NpcCreateResult result = getHttpClient().post(CloudUrlConstant.CREATE_NPC_URL, createData, null, NpcCreateResult.class);
        return result;
    }

    public boolean delete(NpcDeleteData deleteData) {
        heavyConcurrentWait();

        NpcDeleteData result = getHttpClient().delete(CloudUrlConstant.DELETE_NPC_URL, deleteData, null, NpcDeleteData.class);
        return true;
    }
}
