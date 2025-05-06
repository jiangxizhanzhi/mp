package cn.cyberpeace.mp.base.snmp_device.cisco;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.snmp_device.BasicSnmpDevice;

/**
 * asa snmp配置指南：https://community.cisco.com/t5/security-knowledge-base/snmp-configuration-verification-and-troubleshooting-on-asa/ta-p/4300068#toc-hId--271430661
 * asa支持的MIB库范围：https://github.com/cisco/cisco-mibs/blob/main/supportlists/asa/asa-supportlist.html
 */
public class CiscoAsaFirewall extends BasicSnmpDevice {

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
