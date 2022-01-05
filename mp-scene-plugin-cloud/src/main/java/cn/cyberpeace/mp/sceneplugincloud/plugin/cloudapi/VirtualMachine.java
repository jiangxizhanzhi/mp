package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.*;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;

public class VirtualMachine extends CloudClient {

    public VirtualMachineCreateData create(VirtualMachineCreateData createData) {
        heavyConcurrentWait();

        CloudResponse<VirtualMachineCreateData> result = cloudPost(CloudUrlConstant.VIRTUAL_MACHINE_CREATE_URL, createData,
                null, new TypeReference<CloudResponse<VirtualMachineCreateData>>() {
                }, true);
        return result.getData();
    }

    public boolean delete(VirtualMachineDeleteData deleteData) {
        heavyConcurrentWait();

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

    public boolean resume(String instanceId) {
        return doAction(instanceId, new VirtualMachineResumeData());
    }

    public boolean suspend(String instanceId) {
        return doAction(instanceId, new VirtualMachineSuspendData());
    }

    public ConsoleInfoGetResult getConsoleInfo(String instanceId, String hostname) {
        var param = new ConsoleInfoGetParam();
        param.setInstanceUuid(instanceId);
        param.setHostname(hostname);
        return getHttpClient().get(CloudUrlConstant.CONSOLE_INFO_GET_URL, param,
                ConsoleInfoGetResult.class);
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
