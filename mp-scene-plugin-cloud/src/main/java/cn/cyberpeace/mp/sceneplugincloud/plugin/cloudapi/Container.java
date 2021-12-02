package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerPauseData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerResumeData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerSuspendData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerUnPauseData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineLifeControllData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.Field;
import java.util.Collections;

public class Container extends CloudClient {

    public ContainerCreateData create(ContainerCreateData createData) {
        CloudResponse<ContainerCreateData> result = cloudPost(CloudUrlConstant.CONTAINER_CREATE_URL, createData, null,
                new TypeReference<CloudResponse<ContainerCreateData>>() {
                }, true);
        return result.getData();
    }

    public boolean delete(ContainerDeleteData deleteData) {
        CloudResponse<Object> result = cloudDelete(CloudUrlConstant.CONTAINER_DELETE_URL, deleteData, null,
                new TypeReference<CloudResponse<Object>>() {
                }, true);
        return true;
    }

    public boolean lifeControl(String instanceId, String action) {
        VirtualMachineLifeControllData lifeControllData = new VirtualMachineLifeControllData();
        try {
            Field field = lifeControllData.getClass().getDeclaredField(action);
            field.setAccessible(true);
            field.set(lifeControllData, Collections.EMPTY_MAP);
            cloudPost(CloudUrlConstant.CONTAINER_LIFE_CONTROL_URL.replace("{instance_id}", instanceId), lifeControllData, null, new TypeReference<CloudResponse<Object>>() {
            }, true);
            return true;
        } catch (Exception e) {
            throw new MpRuntimeException(e.getMessage());
        }
    }

    public boolean pause(String instanceId) {
        return this.doAction(instanceId, new ContainerPauseData());
    }

    public boolean unPause(String instanceId) {
        return this.doAction(instanceId, new ContainerUnPauseData());
    }

    public boolean suspend(String instanceId) {
        return this.doAction(instanceId, new ContainerSuspendData());
    }

    public boolean resume(String instanceId) {
        return this.doAction(instanceId, new ContainerResumeData());
    }

    private boolean doAction(String instanceId, CloudHttpData httpData) {
        try {
            cloudPost(CloudUrlConstant.CONTAINER_LIFE_CONTROL_URL.replace("{instance_id}", instanceId), httpData, null, new TypeReference<CloudResponse<Object>>() {
            }, true);
            return true;
        } catch (Exception e) {
            throw new MpRuntimeException(e.getMessage());
        }
    }
}
