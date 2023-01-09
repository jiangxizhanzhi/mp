package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.http.HttpParam;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.CommonQueryParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.CreateNs3Data;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.DeleteNs3Data;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NpcCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NpcCreateResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NpcDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NpcGetData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NS3 extends CloudClient {

    private String createNs3Cmds = "ip tuntap add ${tapName} mod tap && ifconfig ${tapName} 0.0.0.0 up && " +
            "ovs-vsctl add-port br-vlan ${tapName} && ovs-vsctl set port ${tapName} tag=${Tag} && " +
            "sh /root/ns3/run.sh start ${tapName} --gateway=${gateway} --ips=${npcIps}";

    private String deleteNs3Cmds = "docker exec ns3 pkill -f \"/home/build/scratch/main --npcIps=${npcIps} --tapName=${tapName} --gateway=${gateway}\"" +
            "ovs-vsctl del-port br-vlan ${tapName}" +
            "ip tuntap del ${tapName} mod tap";

    public NS3() {
        super();
    }

    public void createNs3(String gateway, List<String> ips, String tag, String tapName) {

        List<String> npcIps = new ArrayList<>();
        for (String i : ips) {
            npcIps.add(getNPCIP(i));
        }

        String cmd = StringUtils.replace(createNs3Cmds, "${tapName}", tapName);
        cmd = StringUtils.replace(cmd, "${Tag}", tag);
        cmd = StringUtils.replace(cmd, "${npcIps}", StringUtils.join(npcIps, ","));
        cmd = StringUtils.replace(cmd, "${gateway}", gateway);

        for (String i : ips) {
            cmd = cmd + " && sh /root/ns3/run.sh exec " + tapName + " " + i + " start";
        }

        CreateNs3Data createData = new CreateNs3Data();
        createData.setCmd(cmd);
        createData.setHostname("controller");
        CloudResponse<Object> result = cloudPost(CloudUrlConstant.CREATE_NS3_URL, createData, null,
                new TypeReference<CloudResponse<Object>>() {
                }, true);
        // todo
        System.out.println(result.isSuccess());
    }

    public static String getNPCIP(String ip) {
        if (ip.equals("192.168.1.11")) {
            return "192.168.1.11:tcp:22:192.168.1.10:22";
        } else if (ip.equals("192.168.2.11")) {
            return "192.168.2.11:tcp:22:192.168.2.10:22";
        }
        return ip;
    }

    public void deleteNs3(String gateway, List<String> ips, String tag, String tagName) {
        String cmd = StringUtils.replace(createNs3Cmds, "${tapName}", tagName);
        cmd = StringUtils.replace(cmd, "${Tag}", tag);
        cmd = StringUtils.replace(cmd, "${npcIps}", StringUtils.join(ips, ","));
        cmd = StringUtils.replace(cmd, "${gateway}", gateway);
        DeleteNs3Data createData = new DeleteNs3Data();
        createData.setCmd(cmd);
        createData.setHostname("controller");
        CloudResponse<Object> result = cloudPost(CloudUrlConstant.DELETE_NS3_URL, createData, null,
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
