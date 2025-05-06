package cn.cyberpeace.mp.base.snmp_device;

import cn.cyberpeace.mp.base.util.snmp.SnmpAccessInfo;

public interface SnmpDevice {

    SnmpAccessInfo getSnmpAccessInfo();

    String getHostIp();
    String getCommunity();


    /**
     * 获取设备的镜像名称
     * @return
     */
    String getImageName();

    /**
     * 获取设备系统的详细描述
     * @return
     */
    String getSystemDesc();

    String getSystemVersion();


    /**
     * 获取设备的漏洞信息
     * @return
     */
    String getVulnerabilities();

    /**
     * 获取设备的CPU使用率
     * @return
     */
    int getCpuUsage();

    int memoryUsage();

    int diskUsage();
}
