package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineLifeControllData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.Field;
import java.util.Collections;

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

    public boolean lifeControl(String instanceId, String action) {
        VirtualMachineLifeControllData lifeControllData = new VirtualMachineLifeControllData();
        try {
            Field field = lifeControllData.getClass().getDeclaredField(action);
            field.setAccessible(true);
            field.set(lifeControllData, Collections.EMPTY_MAP);
            cloudPost(CloudUrlConstant.VIRTUAL_MACHINE_LIFE_CONTROL_URL.replace("{instance_id}", instanceId), lifeControllData, null, new TypeReference<CloudResponse<Object>>() {
            }, true);
            return true;
        } catch (Exception e) {
            throw new MpRuntimeException(e.getMessage());
        }
    }
}
