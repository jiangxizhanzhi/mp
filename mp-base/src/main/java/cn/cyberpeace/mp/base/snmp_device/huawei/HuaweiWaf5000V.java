package cn.cyberpeace.mp.base.snmp_device.huawei;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.snmp_device.BasicSnmpDevice;
import cn.cyberpeace.mp.base.util.snmp.SnmpAccessInfo;
import cn.cyberpeace.mp.base.util.snmp.SnmpTaskServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * huawei WAF5000-v-4 https://support.huawei.com/hedex/hdx.do?docid=EDOC1100103264&id=ZH-CN_TOPIC_0183849758
 */
@Slf4j
public class HuaweiWaf5000V extends BasicSnmpDevice {
    public HuaweiWaf5000V(String hostIp) {
        this.snmpAccessInfo = new SnmpAccessInfo();
        this.snmpAccessInfo.setVersion("v3");
        this.snmpAccessInfo.setSecLevel(3);
        this.snmpAccessInfo.setUsername("root");
        this.snmpAccessInfo.setHostIp(hostIp);
        this.snmpAccessInfo.setAuthProtocol("MD5");
        this.snmpAccessInfo.setAuthPassPhrase("QWEasd123#");
        this.snmpAccessInfo.setPrivacyProtocol("DES");
        this.snmpAccessInfo.setPrivacyPassPhrase("QWEasd123#");
    }

    @Override
    public String getSystemDesc() {
        return "HUAWEI WAF5000-V-4 V200R001C00";
    }
    @Override
    protected String doGetSystemDescOid() {
        throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("当前设备不支持通过snmp查询系统描述信息"));
    }

    @Override
    public int getCpuUsage() {
        try {
            return 100 - Integer.parseInt(Objects.requireNonNull(SnmpTaskServiceImpl.getSnmpData(getSnmpAccessInfo(), doGetCpuIdlePercentOid())));
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    protected String doGetCpuIdlePercentOid() {
        return ".1.3.6.1.4.1.2021.11.11.0";
    }

    @Override
    protected String doGetCpuUsageOid() {
        return ".1.3.6.1.4.1.2021.11.10.0";
    }

    @Override
    protected String doGetMemoryFreeSizeOid() {
        return ".1.3.6.1.4.1.2021.4.11.0";
    }

    @Override
    protected String doGetMemoryUsedSizeOid() {
        return ".1.3.6.1.4.1.2021.4.6.0";
    }

    @Override
    protected String doGetFlashFreeSizeOid() {
        throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("当前设备不支持通过snmp查询flash剩余空间"));
    }

    @Override
    protected String doGetFlashTotalSizeOid() {
        throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("当前设备不支持通过snmp查询flash总空间"));
    }

    @Override
    public String getImageName() {
        return "HUAWEI_WAF5000-V-4";
    }

    @Override
    public String getSystemVersion() {
        return "HUAWEI WAF5000-V-4 V200R001C00";
    }
}
