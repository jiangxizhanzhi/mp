package cn.cyberpeace.mp.base.snmp_device.cisco;

public class CiscoRouter extends BasicCiscoDevice {
    public CiscoRouter(String hostIp, String community) {
        this.hostIp = hostIp;
        this.community = community;
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
