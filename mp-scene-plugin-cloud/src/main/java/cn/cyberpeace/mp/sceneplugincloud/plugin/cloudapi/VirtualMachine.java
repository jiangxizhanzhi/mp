package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineDeleteData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;

public class VirtualMachine extends CloudClient {

    public VirtualMachineCreateData create(VirtualMachineCreateData createData) {
        CloudResponse<VirtualMachineCreateData> result = getHttpClient().successPost(
                CloudUrlConstant.VIRTUAL_MACHINE_CREATE_URL, createData, null,
                new TypeReference<CloudResponse<VirtualMachineCreateData>>() {});
        return result.getData();
    }

    public boolean delete(VirtualMachineDeleteData deleteData) {
        CloudResponse<Object> result = getHttpClient().successDelete(CloudUrlConstant.VIRTUAL_MACHINE_DELETE_URL,
                deleteData, null, new TypeReference<CloudResponse<Object>>() {});
        return true;
    }
}
