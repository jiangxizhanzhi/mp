package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.DynamipsInfoItems;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.DynamipsInfoParam;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualDynamicActionData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualDynamicCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualDynamicDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class VirtualDynamips extends CloudClient {

    public VirtualDynamicCreateData create(VirtualDynamicCreateData createData) {
        heavyConcurrentWait();
        VirtualDynamicCreateData response = getHttpClient().post(CloudUrlConstant.CREATE_DYNAMIPS, createData, null, new TypeReference<>() {
        });
        return response;
    }

    public void delete(List<String> resourceIds) {
        heavyConcurrentWait();
        VirtualDynamicDeleteData deleteData = new VirtualDynamicDeleteData();
        deleteData.setVms(resourceIds);
        cloudDelete(CloudUrlConstant.DELETE_DYNAMIPS, deleteData, null, new TypeReference<CloudResponse<Object>>() {
        }, true);
    }

    public boolean restart(String instanceId) {
        var actionData = new VirtualDynamicActionData();
        actionData.setAction(VirtualDynamicActionData.RESTART);
        return doAction(instanceId, actionData);
    }

    public boolean pause(String instanceId) {
        var actionData = new VirtualDynamicActionData();
        actionData.setAction(VirtualDynamicActionData.SUSPEND);
        return doAction(instanceId, actionData);
    }

    public boolean unPause(String instanceId) {
        var actionData = new VirtualDynamicActionData();
        actionData.setAction(VirtualDynamicActionData.RESUME);
        return doAction(instanceId, actionData);
    }

    public boolean suspend(String instanceId) {
        var actionData = new VirtualDynamicActionData();
        actionData.setAction(VirtualDynamicActionData.SUSPEND);
        return doAction(instanceId, actionData);
    }

    public boolean resume(String instanceId) {
        var actionData = new VirtualDynamicActionData();
        actionData.setAction(VirtualDynamicActionData.RESUME);
        return doAction(instanceId, actionData);
    }

    public boolean start(String instanceId) {
        var actionData = new VirtualDynamicActionData();
        actionData.setAction(VirtualDynamicActionData.START);
        return doAction(instanceId, actionData);
    }

    public boolean stop(String instanceId) {
        var actionData = new VirtualDynamicActionData();
        actionData.setAction(VirtualDynamicActionData.STOP);
        return doAction(instanceId, actionData);
    }

    public DynamipsInfoItems getInfo() {
        DynamipsInfoParam type = new DynamipsInfoParam();
        type.setType("port");
        var result = getHttpClient().get(CloudUrlConstant.GET_DYNAMIPS_INFO, type, new TypeReference<DynamipsInfoItems>() {
        });
        return result;
    }

    public void sendRule(String resourceId, List<String> cmds) {

    }

    private boolean doAction(String instanceId, CloudHttpData httpData) {
        try {
            cloudPost(CloudUrlConstant.VIRTUAL_DYNAMIPS_LIFE_CONTROL_URL.replace("{instance_id}", instanceId), httpData, null, new TypeReference<CloudResponse<Object>>() {
            }, true);
            return true;
        } catch (Exception e) {
            throw new MpRuntimeException(e.getMessage());
        }
    }
}
