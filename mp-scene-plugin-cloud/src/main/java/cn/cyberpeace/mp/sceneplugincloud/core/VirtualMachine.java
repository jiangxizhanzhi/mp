package cn.cyberpeace.mp.sceneplugincloud.core;

import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.core.common.CloudClient;
import cn.cyberpeace.mp.sceneplugincloud.core.param.VirtualMachineCreateData;
import cn.cyberpeace.mp.sceneplugincloud.core.param.VirtualMachineDeleteData;

public class VirtualMachine extends CloudClient {

    public void create(VirtualMachineCreateData createData) {
        getHttpClient().successPost(CloudUrlConstant.VIRTUAL_MACHINE_CREATE_URL, createData, null);
    }

    public void delete(VirtualMachineDeleteData deleteData) {
        getHttpClient().successDelete(CloudUrlConstant.VIRTUAL_MACHINE_DELETE_URL, deleteData, null);
    }
}
