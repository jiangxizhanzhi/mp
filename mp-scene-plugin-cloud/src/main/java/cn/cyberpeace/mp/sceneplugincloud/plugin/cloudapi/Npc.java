package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.http.HttpParam;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.CommonQueryParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.CreateNs3Data;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NpcCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NpcCreateResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NpcDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NpcGetData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Npc extends CloudClient {

    private String createNs3Cmds = "ip tuntap add ${tapName} mod tap && ifconfig ${tapName} 0.0.0.0 up && " +
            "ovs-vsctl add-port br-vlan ${tapName} && ovs-vsctl set port ${tapName} tag=${Tag} && " +
            "docker exec ns3 ./waf --run=\"main --npcIps=${npcIps} --tapName=${tapName} --gateway=${gateway}\" >> /var/log/test.log &";

    public Npc() {
        super();
    }

    public void createNs3(String gateway, List<String> ips, String tagName) {
        String cmd = StringUtils.replace(createNs3Cmds, "${tapName}", "tap_" + TextUtils.uuid());
        cmd = StringUtils.replace(cmd, "${Tag}", tagName);
        cmd = StringUtils.replace(cmd, "${npcIps}", StringUtils.join(ips, ","));
        cmd = StringUtils.replace(cmd, "${gateway}", gateway);
        CreateNs3Data createData = new CreateNs3Data();
        createData.setCmd(cmd);
        createData.setHost("controller");
        CloudResponse<Object> result = cloudPost(CloudUrlConstant.CREATE_NS3_URL, createData, null,
                new TypeReference<CloudResponse<Object>>() {
                }, true);
        // todo
        System.out.println(result.isSuccess());
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

        CloudResponse<Object> result = cloudDelete(CloudUrlConstant.DELETE_NPC_URL, deleteData, null,
                new TypeReference<CloudResponse<Object>>() {
                }, true);
        return true;
    }

    public List<NpcGetData> getAllNpc(Integer limit, Integer offset) {
        CommonQueryParam param = new CommonQueryParam();
        param.setLimit(limit);
        param.setOffset(offset);
        CloudResponse<List<NpcGetData>> result = cloudGet(CloudUrlConstant.GET_NPC_URL, param,
                new TypeReference<CloudResponse<List<NpcGetData>>>() {
                }, true);
        if (result != null && result.getData() != null && result.getData().size() > 0) {
            return result.getData();
        }
        return null;
    }
}
