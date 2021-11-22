package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachinePauseData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineRestartData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineUnPauseData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;

public class VirtualMachine extends CloudClient {

    public VirtualMachineCreateData create(VirtualMachineCreateData createData) {
        CloudResponse<VirtualMachineCreateData> result = cloudPost(CloudUrlConstant.VIRTUAL_MACHINE_CREATE_URL, createData,
                null, new TypeReference<CloudResponse<VirtualMachineCreateData>>() {
                }, true);
        return result.getData();
    }

    public boolean delete(VirtualMachineDeleteData deleteData) {
        CloudResponse<Object> result = cloudDelete(CloudUrlConstant.VIRTUAL_MACHINE_DELETE_URL, deleteData, null,
                new TypeReference<CloudResponse<Object>>() {
                }, true);
        return true;
    }

    public boolean pause(String instanceId) {
        return doAction(instanceId, new VirtualMachinePauseData());
    }

    public boolean unpause(String instanceId) {
        return doAction(instanceId, new VirtualMachineUnPauseData());
    }

    public boolean restart(String instanceId) {
        return doAction(instanceId, new VirtualMachineRestartData());
    }

    private boolean doAction(String instanceId, CloudHttpData httpData) {
        try {
            cloudPost(CloudUrlConstant.VIRTUAL_MACHINE_LIFE_CONTROL_URL.replace("{instance_id}", instanceId), httpData, null, new TypeReference<CloudResponse<Object>>() {
            }, true);
            return true;
        } catch (Exception e) {
            throw new MpRuntimeException(e.getMessage());
        }
    }
}
