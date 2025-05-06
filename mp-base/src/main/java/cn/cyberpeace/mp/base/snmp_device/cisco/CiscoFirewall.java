package cn.cyberpeace.mp.base.snmp_device.cisco;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;

public class CiscoFirewall extends BasicCiscoDevice{

    public CiscoFirewall(String hostIp, String community) {
        this.hostIp = hostIp;
        this.community = community;
    }

    @Override
    public String getImageName() {
        return "Cisco-ASA5520";
    }

    @Override
    public String getSystemVersion() {
        return "Cisco Adaptive Security Appliance Version 9.12(4)37";
    }

    @Override
    protected String doGetSystemDescOid() {
        return ".1.3.6.1.2.1.1.1.0";
    }

    @Override
    protected String doGetCpuUsageOid() {
        return ".1.3.6.1.4.1.9.9.109.1.1.1.1.7.4";
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
        throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("当前设备不支持通过snmp查询flash信息"));
    }

    @Override
    protected String doGetFlashTotalSizeOid() {
        throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("当前设备不支持通过snmp查询flash信息"));
    }
}
