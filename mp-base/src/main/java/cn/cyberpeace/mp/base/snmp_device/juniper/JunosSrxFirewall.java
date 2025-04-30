package cn.cyberpeace.mp.base.snmp_device.juniper;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.snmp_device.BasicSnmpDevice;
import cn.cyberpeace.mp.base.util.snmp.SnmpAccessInfo;
import cn.cyberpeace.mp.base.util.snmp.SnmpTaskServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class JunosSrxFirewall extends BasicSnmpDevice {

    public JunosSrxFirewall(String hostIp) {
        SnmpAccessInfo snmpAccessInfo = new SnmpAccessInfo();
        snmpAccessInfo.setHostIp(hostIp);
        snmpAccessInfo.setVersion("v2c");
        snmpAccessInfo.setCommunity("public");
        this.snmpAccessInfo = snmpAccessInfo;
    }

    /**
     * Name/OID: sysDescr.0; Value (OctetString): Juniper Networks, Inc. firefly-perimeter internet router, kernel JUNOS 12.1X47-D20.7 #0: 2015-03-03 21:53:50 UTC     builder@chamuth.juniper.net:/volume/build/junos/12.1/service/12.1X47-D20.7/obj-i386/junos/bsd/kernels/VSRX/kernel Build date: 2015-03-03
     * @return
     */
    @Override
    protected String doGetSystemDescOid() {
        return ".1.3.6.1.2.1.1.1.0";
    }

    /**
     * Name/OID: jnxOperating5MinLoadAvg.9.1.0.0; Value (Gauge): 1
     * @return
     */
    @Override
    protected String doGetCpuUsageOid() {
        return ".1.3.6.1.4.1.2636.3.1.13.1.21.9.1.0.0";
    }

    @Override
    public int memoryUsage() {
        try {
            long memoryUsedSizeBits = Long.parseLong(Objects.requireNonNull(SnmpTaskServiceImpl.getSnmpData(getSnmpAccessInfo(),
                    doGetMemoryUsedSizeOid())));
            long memoryTotalSizeBits = Long.parseLong(Objects.requireNonNull(SnmpTaskServiceImpl.getSnmpData(getSnmpAccessInfo(),
                    doGetMemoryFullSizeOid())));
            return (int) ((memoryUsedSizeBits * 100 / memoryTotalSizeBits));
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Name/OID: jnxOperatingMemory.9.1.0.0; Value (Integer): 2048
     * @return
     */
    protected String doGetMemoryFullSizeOid() {
        return ".1.3.6.1.4.1.2636.3.1.13.1.15.9.1.0.0";
    }

    @Override
    protected String doGetMemoryFreeSizeOid() {
        throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("not support"));
    }

    /**
     * Name/OID: jnxOperatingMemoryCP.9.1.0.0; Value (Integer): 1150
     * @return
     */
    @Override
    protected String doGetMemoryUsedSizeOid() {
        return ".1.3.6.1.4.1.2636.3.1.13.1.24.9.1.0.0";
    }

    @Override
    public int diskUsage() {
        try {
            long flashUsedSizeBits = flashUsedSize();
            long flashTotalSizeBits = flashFullSize();
            return (int) ((flashUsedSizeBits * 100 / flashTotalSizeBits));
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    protected long flashFullSize() {
        List<Long> unitSizes = unitSizes();
        List<Long> fullUnits = fullUnits();
        if (unitSizes.size() != fullUnits.size()) {
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("flashFullSize size not match"));
        }
        long flashFullSizeBits = 0;
        for (int i = 0; i < unitSizes.size(); i++) {
            flashFullSizeBits += unitSizes.get(i) * fullUnits.get(i);
        }
        return flashFullSizeBits;
    }

    protected long flashUsedSize() {
        List<Long> unitSizes = unitSizes();
        List<Long> usedUnits = usedUnits();
        if (unitSizes.size()!= usedUnits.size()) {
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("flashUsedSize size not match"));
        }
        long flashUsedSizeBits = 0;
        for (int i = 0; i < unitSizes.size(); i++) {
            flashUsedSizeBits += unitSizes.get(i) * usedUnits.get(i);
        }
        return flashUsedSizeBits;
    }


    /**
     *  .1.3.6.1.2.1.25.2.3.1.4
     * @return
     */
    private List<Long> unitSizes() {
        return SnmpTaskServiceImpl.walkSnmpData(this.snmpAccessInfo, ".1.3.6.1.2.1.25.2.3.1.4")
                .stream().map(Long::parseLong).collect(Collectors.toList());
    }

    private List<Long> usedUnits() {
        return SnmpTaskServiceImpl.walkSnmpData(this.snmpAccessInfo, ".1.3.6.1.2.1.25.2.3.1.6")
                .stream().map(Long::parseLong).collect(Collectors.toList());
    }

    private List<Long> fullUnits() {
        return SnmpTaskServiceImpl.walkSnmpData(this.snmpAccessInfo, ".1.3.6.1.2.1.25.2.3.1.5")
                .stream().map(Long::parseLong).collect(Collectors.toList());
    }

    @Override
    protected String doGetFlashFreeSizeOid() {
        throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("not support"));
    }

    @Override
    protected String doGetFlashTotalSizeOid() {
        throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("not support"));
    }

    @Override
    public String getImageName() {
        return "Junos-srx";
    }

    @Override
    public String getSystemVersion() {
        return "Juniper Networks, Inc. firefly-perimeter internet router, kernel JUNOS 12.1X47-D20.7 #0: 2015-03-03 21:53:50 UTC     builder@chamuth.juniper.net:/volume/build/junos/12.1/service/12.1X47-D20.7/obj-i386/junos/bsd/kernels/VSRX/kernel Build date: 2015-03-03";
    }
}
