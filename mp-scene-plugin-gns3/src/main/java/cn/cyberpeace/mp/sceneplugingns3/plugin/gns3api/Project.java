package cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api;

import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.CmdExecData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import cn.cyberpeace.mp.sceneplugingns3.constant.Gns3UrlConstant;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.common.Gns3Client;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.CreateProjectParams;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.CreateProjectResult;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

public class Project extends Gns3Client {
    public String create(String name) {
        CreateProjectParams projectParams = new CreateProjectParams();
        projectParams.setName(name);
        var result = getHttpClient().post(Gns3UrlConstant.CREATE_PROJECT, projectParams, null, CreateProjectResult.class);
        return result.getProjectId();
    }

    public void delete(String id) {
        String url = Gns3UrlConstant.DELETE_PROJECT.replace("{project_id}", id);
        try {
            getHttpClient().delete(url, null, null, Object.class);
        } catch (Exception e) {
            if (!"Entity may not be null".equals(e.getMessage())) {
                throw e;
            }
        }
    }

    public void start(String id) {
        String url = Gns3UrlConstant.START_PROJECT.replace("{project_id}", id);
        try {
            getHttpClient().post(url, null, null, Object.class);
        } catch (Exception e) {
            if (!"Entity may not be null".equals(e.getMessage())) {
                throw e;
            }
        }
    }

    public void stop(String id) {
        String url = Gns3UrlConstant.STOP_PROJECT.replace("{project_id}", id);
        try {
            getHttpClient().post(url, null, null, Object.class);
        } catch (Exception e) {
            if (!"Entity may not be null".equals(e.getMessage())) {
                throw e;
            }
        }
    }

    public void createBridge(String name) {
        List<String> cmds = new ArrayList<>();
        cmds.add("ip tuntap add " + name + " mod tap");
        cmds.add("ifconfig " + name + " 0.0.0.0 up");
        cmds.add("ovs-vsctl add-port br-vlan " + name);

        for (String cmd : cmds) {
            CmdExecData cmdExecData = new CmdExecData();
            cmdExecData.setHostname("controller");
            cmdExecData.setCmd(cmd);
            new Remote().cloudPost(Gns3UrlConstant.EXECCMD, cmdExecData, null, new TypeReference<CloudResponse<Object>>() {
            }, true);
        }
    }

    public void deleteBridge(String name) {
        CmdExecData cmdExecData = new CmdExecData();
        cmdExecData.setCmd("ip tuntap del " + name + " mod tap");
        cmdExecData.setHostname("controller");
        try {
            new Remote().cloudPost(Gns3UrlConstant.EXECCMD, cmdExecData, null, new TypeReference<CloudResponse<Object>>() {
            }, true);
        } catch (Exception e) {
            if (!"Entity may not be null".equals(e.getMessage())) {
                throw e;
            }
        }
    }
}
