package cn.cyberpeace.mp.base.snmp_device.cisco;

import cn.cyberpeace.mp.base.snmp_device.SnmpDevice;
import cn.cyberpeace.mp.base.util.snmp.SnmpAccessInfo;
import cn.cyberpeace.mp.base.util.snmp.SnmpTaskServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public abstract class BasicCiscoDevice implements SnmpDevice {

    String hostIp;
    String community;
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
    public String getSystemDesc() {
        try {
            return SnmpTaskServiceImpl.getSnmpData(getSnmpAccessInfo(this.hostIp, this.community), doGetSystemDescOid());
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    protected abstract String doGetSystemDescOid();

    @Override
    public String getVulnerabilities() {
        return "";
    }

    @Override
    public int getCpuUsage() {
        try {
            return Integer.parseInt(Objects.requireNonNull(SnmpTaskServiceImpl.getSnmpData(getSnmpAccessInfo(this.hostIp, this.community), doGetCpuUsageOid())));
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    protected abstract String doGetCpuUsageOid();

    @Override
    public int memoryUsage() {
        try {
            long memoryFreeSizeBits = Long.parseLong(Objects.requireNonNull(SnmpTaskServiceImpl.getSnmpData(getSnmpAccessInfo(this.hostIp, this.community),
                    doGetMemoryFreeSizeOid())));
            long memoryUsedSizeBits = Long.parseLong(Objects.requireNonNull(SnmpTaskServiceImpl.getSnmpData(getSnmpAccessInfo(this.hostIp, this.community),
                    doGetMemoryUsedSizeOid())));
            long memoryTotalSizeBits = memoryFreeSizeBits + memoryUsedSizeBits;
            return (int) ((memoryUsedSizeBits * 100 / memoryTotalSizeBits));
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    protected abstract String doGetMemoryFreeSizeOid();
    protected abstract String doGetMemoryUsedSizeOid();

    @Override
    public int diskUsage() {
        try {
            long flashFreeSizeBits = Long.parseLong(Objects.requireNonNull(SnmpTaskServiceImpl.getSnmpData(getSnmpAccessInfo(this.hostIp, this.community),
                    doGetFlashFreeSizeOid())));
            long flashTotalSizeBits = Long.parseLong(Objects.requireNonNull(SnmpTaskServiceImpl.getSnmpData(getSnmpAccessInfo(this.hostIp, this.community),
                    doGetFlashTotalSizeOid())));
            long flashUsedSizeBits = flashTotalSizeBits - flashFreeSizeBits;
            return (int) ((flashUsedSizeBits * 100 / flashTotalSizeBits));
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    protected abstract String doGetFlashFreeSizeOid();
    protected abstract String doGetFlashTotalSizeOid();
}
