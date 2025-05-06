package cn.cyberpeace.mp.base.snmp_device;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.snmp.SnmpAccessInfo;
import cn.cyberpeace.mp.base.util.snmp.SnmpTaskServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public abstract class BasicSnmpDevice implements SnmpDevice {
    protected SnmpAccessInfo snmpAccessInfo;
    @Override
    public SnmpAccessInfo getSnmpAccessInfo() {
        if (Objects.isNull(snmpAccessInfo)) {
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("snmp认证信息不能为空"));
        }
        return snmpAccessInfo;
    }

    @Override
    public String getHostIp() {
        return this.snmpAccessInfo.getHostIp();
    }

    @Override
    public String getCommunity() {
        return this.snmpAccessInfo.getCommunity();
    }

    @Override
    public String getSystemDesc() {
        try {
            return SnmpTaskServiceImpl.getSnmpData(getSnmpAccessInfo(), doGetSystemDescOid());
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
            return Integer.parseInt(Objects.requireNonNull(SnmpTaskServiceImpl.getSnmpData(getSnmpAccessInfo(), doGetCpuUsageOid())));
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    protected abstract String doGetCpuUsageOid();

    @Override
    public int memoryUsage() {
        try {
            long memoryFreeSizeBits = Long.parseLong(Objects.requireNonNull(SnmpTaskServiceImpl.getSnmpData(getSnmpAccessInfo(),
                    doGetMemoryFreeSizeOid())));
            long memoryUsedSizeBits = Long.parseLong(Objects.requireNonNull(SnmpTaskServiceImpl.getSnmpData(getSnmpAccessInfo(),
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
            long flashFreeSizeBits = Long.parseLong(Objects.requireNonNull(SnmpTaskServiceImpl.getSnmpData(getSnmpAccessInfo(),
                    doGetFlashFreeSizeOid())));
            long flashTotalSizeBits = Long.parseLong(Objects.requireNonNull(SnmpTaskServiceImpl.getSnmpData(getSnmpAccessInfo(),
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
