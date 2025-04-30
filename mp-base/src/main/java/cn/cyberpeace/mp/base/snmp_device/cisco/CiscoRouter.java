package cn.cyberpeace.mp.base.snmp_device.cisco;

import cn.cyberpeace.mp.base.snmp_device.SnmpDevice;
import cn.cyberpeace.mp.base.util.snmp.SnmpAccessInfo;
import cn.cyberpeace.mp.base.util.snmp.SnmpTaskServiceImpl;
import lombok.AllArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
public class CiscoRouter implements SnmpDevice {
    private String hostIp;
    private String community;
    @Override
    public SnmpAccessInfo getSnmpAccessInfo(String host, String community) {
        SnmpAccessInfo snmpAccessInfo = new SnmpAccessInfo();
        snmpAccessInfo.setHostIp(host);
        snmpAccessInfo.setCommunity(community);
        snmpAccessInfo.setVersion("v2c");
        return snmpAccessInfo;
    }

    @Override
    public String getHostIp() {
        return this.hostIp;
    }

    @Override
    public String getCommunity() {
        return this.community;
    }

    @Override
    public String getImageName() {
        return "Cisco-CSR1000V";
    }

    @Override
    public String getSystemDesc() {
        return SnmpTaskServiceImpl.getSnmpData(getSnmpAccessInfo(this.hostIp, this.community), ".1.3.6.1.2.1.1.1.0");
    }

    @Override
    public String getVulnerabilities() {
        return "";
    }

    @Override
    public int getCpuUsage() {
        return Integer.parseInt(Objects.requireNonNull(SnmpTaskServiceImpl.getSnmpData(getSnmpAccessInfo(this.hostIp, this.community), ".1.3.6.1.4.1.9.9.109.1.1.1.1.7.7")));
    }

    @Override
    public int memoryUsage() {
        long memoryFreeSizeBits = Long.parseLong(Objects.requireNonNull(SnmpTaskServiceImpl.getSnmpData(getSnmpAccessInfo(this.hostIp, this.community),
                ".1.3.6.1.4.1.9.9.48.1.1.1.6.1")));
        long memoryUsedSizeBits = Long.parseLong(Objects.requireNonNull(SnmpTaskServiceImpl.getSnmpData(getSnmpAccessInfo(this.hostIp, this.community),
                ".1.3.6.1.4.1.9.9.48.1.1.1.5.1")));
        long memoryTotalSizeBits = memoryFreeSizeBits + memoryUsedSizeBits;
        return (int) ((memoryUsedSizeBits * 100 / memoryTotalSizeBits));
    }

    @Override
    public int diskUsage() {
        long flashFreeSizeBits = Long.parseLong(Objects.requireNonNull(SnmpTaskServiceImpl.getSnmpData(getSnmpAccessInfo(this.hostIp, this.community),
                ".1.3.6.1.4.1.9.9.10.1.1.4.1.1.14.1.1")));
        long flashTotalSizeBits = Long.parseLong(Objects.requireNonNull(SnmpTaskServiceImpl.getSnmpData(getSnmpAccessInfo(this.hostIp, this.community),
                ".1.3.6.1.4.1.9.9.10.1.1.4.1.1.13.1.1")));
        long flashUsedSizeBits = flashTotalSizeBits - flashFreeSizeBits;
        return (int) ((flashUsedSizeBits * 100 / flashTotalSizeBits));
    }
}
