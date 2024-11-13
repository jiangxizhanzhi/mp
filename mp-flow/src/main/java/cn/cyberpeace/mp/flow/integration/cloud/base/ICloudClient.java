package cn.cyberpeace.mp.flow.integration.cloud.base;

public interface ICloudClient {
    /**
     * 创建虚拟机
     */
    void createVirtualMachine(AbstractCreateVirtualMachineParams params);

    /**
     * 删除虚拟机
     */
    void deleteVirtualMachine(AbstractDeleteVirtualMachineParams params);

    /**
     * 添加网卡
     */
    AddVirtualNicResult addVirtualNic(AbstractAddVirtualNicParams params);
}
