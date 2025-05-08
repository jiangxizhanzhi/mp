package cn.cyberpeace.mp.base.snmp_device.juniper;

import cn.cyberpeace.mp.base.snmp_device.BasicSnmpDevice;
import cn.cyberpeace.mp.base.util.snmp.SnmpAccessInfo;

public class JunosOliver12 extends BasicSnmpDevice {

    public JunosOliver12(String hostIp) {
        SnmpAccessInfo snmpAccessInfo = new SnmpAccessInfo();
        snmpAccessInfo.setHostIp(hostIp);
        snmpAccessInfo.setVersion("v2c");
        snmpAccessInfo.setCommunity("public");
        this.snmpAccessInfo = snmpAccessInfo;
    }

    @Override
    protected String doGetSystemDescOid() {
        return ".1.3.6.1.2.1.1.1.0";
    }

    @Override
    protected String doGetCpuUsageOid() {
        return null;
    }

    @Override
    protected String doGetMemoryFreeSizeOid() {
        return null;
    }

    @Override
    protected String doGetMemoryUsedSizeOid() {
        return null;
    }

    @Override
    protected String doGetFlashFreeSizeOid() {
        return null;
    }

    @Override
    protected String doGetFlashTotalSizeOid() {
        return null;
    }

    @Override
    public String getImageName() {
        return null;
    }

    @Override
    public String getSystemVersion() {
        return null;
    }
}
