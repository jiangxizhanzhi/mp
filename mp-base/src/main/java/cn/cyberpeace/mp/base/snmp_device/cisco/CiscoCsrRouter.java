package cn.cyberpeace.mp.base.snmp_device.cisco;

import cn.cyberpeace.mp.base.snmp_device.BasicSnmpDevice;
import cn.cyberpeace.mp.base.util.snmp.SnmpAccessInfo;

/**
 * cisco rsv1000 支持的MIB库https://github.com/cisco/cisco-mibs/v2
 */

public class CiscoCsrRouter extends BasicSnmpDevice {

    public CiscoCsrRouter(String hostIp) {
        SnmpAccessInfo snmpAccessInfo = new SnmpAccessInfo();
        snmpAccessInfo.setHostIp(hostIp);
        snmpAccessInfo.setVersion("v2c");
        snmpAccessInfo.setCommunity("public");
        this.snmpAccessInfo = snmpAccessInfo;
    }

    @Override
    public String getImageName() {
        return "Cisco-CSR1000V";
    }

    @Override
    public String getSystemVersion() {
        return null;
    }

    @Override
    protected String doGetSystemDescOid() {
        return ".1.3.6.1.2.1.1.1.0";
    }

    @Override
    protected String doGetCpuUsageOid() {
        return ".1.3.6.1.4.1.9.9.109.1.1.1.1.7.7";
    }

    @Override
    protected String doGetMemoryFreeSizeOid() {
        return ".1.3.6.1.4.1.9.9.48.1.1.1.6.1";
    }

    @Override
    protected String doGetMemoryUsedSizeOid() {
        return ".1.3.6.1.4.1.9.9.48.1.1.1.5.1";
    }

    @Override
    protected String doGetFlashFreeSizeOid() {
        return ".1.3.6.1.4.1.9.9.10.1.1.4.1.1.14.1.1";
    }

    @Override
    protected String doGetFlashTotalSizeOid() {
        return ".1.3.6.1.4.1.9.9.10.1.1.4.1.1.13.1.1";
    }
}
