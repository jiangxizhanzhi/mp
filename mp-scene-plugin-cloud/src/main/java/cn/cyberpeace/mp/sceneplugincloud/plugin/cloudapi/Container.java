package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.ILifeCycleControlHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineLifeControllData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

public class Container extends CloudClient implements ILifeCycleControlHandler {

    public ContainerCreateData create(ContainerCreateData createData) {
        CloudResponse<ContainerCreateData> result = cloudPost(CloudUrlConstant.CONTAINER_CREATE_URL, createData, null,
                new TypeReference<CloudResponse<ContainerCreateData>>() {}, true);
        return result.getData();
    }

    public boolean delete(ContainerDeleteData deleteData) {
        CloudResponse<Object> result = cloudDelete(CloudUrlConstant.CONTAINER_DELETE_URL, deleteData, null,
                new TypeReference<CloudResponse<Object>>() {}, true);
        return true;
    }

    @Override
    public boolean lifeControl(String instanceId, String action) {
        VirtualMachineLifeControllData lifeControllData = new VirtualMachineLifeControllData();
        try {
            Method method = lifeControllData.getClass().getDeclaredMethod("set" + action.toUpperCase().charAt(0) + action.substring(1), Map.class);
            method.setAccessible(true);
            method.invoke(lifeControllData, Collections.EMPTY_MAP);
            cloudPost(CloudUrlConstant.CONTAINER_LIFE_CONTROL_URL.replace("{instance_id}", instanceId), lifeControllData, null, new TypeReference<CloudResponse<Object>>() {
            }, true);
            return true;
        } catch (Exception e) {
            throw new MpRuntimeException(e.getMessage());
        }
    }
}
