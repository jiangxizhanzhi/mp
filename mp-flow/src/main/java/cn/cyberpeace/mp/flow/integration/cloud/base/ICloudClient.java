package cn.cyberpeace.mp.flow.integration.cloud.base;

public interface ICloudClient {
    GetVirtualMachineResult getVirtualMachine(AbstractGetVirtualMachineParams params);

    /**
     * 创建虚拟机
     */
    GetVirtualMachineResult createVirtualMachine(AbstractCreateVirtualMachineParams params);

    /**
     * 删除虚拟机
     */
    DeleteVirtualMachineResult deleteVirtualMachine(AbstractDeleteVirtualMachineParams params);

    /**
     * 添加网卡
     */
    AddVirtualNicResult addVirtualNic(AbstractAddVirtualNicParams params);
}
