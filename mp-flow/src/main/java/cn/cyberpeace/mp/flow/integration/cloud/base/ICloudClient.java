package cn.cyberpeace.mp.flow.integration.cloud.base;

import java.util.List;

public interface ICloudClient {
    GetVirtualMachineResult getVirtualMachine(String id);

    /**
     * 创建虚拟机
     */
    GetVirtualMachineResult createVirtualMachine();

    /**
     * 删除虚拟机
     */
    DeleteVirtualMachineResult deleteVirtualMachine(String id);

    /**
     * 添加网卡
     */
    AddVirtualNicResult addVirtualNic(AddVirtualNicParams params);
}
