package cn.cyberpeace.mp.cscene.dto.common;

import cn.cyberpeace.mp.base.bo.AttackType;
import cn.cyberpeace.mp.cscene.model.AttackCommandHistoryModel;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class AttackCommandExecuteInfoToThirdDto {

    /**
     * 命令分类id
     */
    private String category;

    /**
     * 对应分类下具体攻击指令描述
     */
    private String attackDesc;

    /**
     * 攻击是否生效
     */
    private boolean attackEffectedFlag;

    /**
     * 命令的原始输出和格式化输出
     */
    private AttackCommandOutput attackCommandOutput;

    public AttackCommandExecuteInfoToThirdDto(AttackType attackType, AttackCommandHistoryModel attackCommandHistoryModel) {
        this.category = attackType.getToolId();
        this.attackDesc = attackType.getAttackDesc();
        this.attackEffectedFlag = attackCommandHistoryModel.getAttackEffectedFlag();
        this.attackCommandOutput = new AttackCommandOutput(attackCommandHistoryModel.getAttackCommandOutput(), attackType.getToolId());
    }


    public static void main(String[] args) {
        AttackCommandHistoryModel attackCommandHistoryModel = new AttackCommandHistoryModel();
        attackCommandHistoryModel.setAttackCommandOutput("Starting Nmap 7.91 ( https://nmap.org ) at 2025-08-14 10:11 EDT\n" +
                "Nmap scan report for 172.140.241.149\n" +
                "Host is up (0.00038s latency).\n" +
                "Not shown: 997 closed ports\n" +
                "PORT     STATE SERVICE       VERSION\n" +
                "22/tcp   open  ssh           OpenSSH 7.4 (protocol 2.0)\n" +
                "514/tcp  open  shell?\n" +
                "3389/tcp open  ms-wbt-server xrdp\n" +
                "\n" +
                "Service detection performed. Please report any incorrect results at https://nmap.org/submit/ .\n" +
                "Nmap done: 1 IP address (1 host up) scanned in 33.14 seconds");
        attackCommandHistoryModel.setAttackEffectedFlag(true);

        AttackCommandExecuteInfoToThirdDto attackCommandExecuteInfoToThirdDto = new AttackCommandExecuteInfoToThirdDto(
                AttackType.XIUTAN, attackCommandHistoryModel);
    }


    @Data
    public static class AttackCommandOutput {
        private String origin;
        private Object format;
        private String toolId;


        public AttackCommandOutput(String origin, String toolId) {
            this.origin = origin;
            if (StringUtils.isNotEmpty(origin) && origin.contains("Nmap")
                    && toolId.equals(AttackType.XIUTAN.getToolId())) {
                this.format = new NmapFormat(origin);
            }
            if (StringUtils.isNotEmpty(origin) && toolId.equals(AttackType.XIUTAN_SQLMAP.getToolId())) {
                this.format = new SqlmapFormat(origin);
            }
        }
    }

    @Data
    public static class NmapFormat {
        private String hostStatus;
        private String macAddr;
        private List<PortAndService> portsAndServices = new ArrayList<>();

        public NmapFormat(String origin) {
            if (StringUtils.isNotEmpty(origin)) {
                if (origin.contains("Host is up")) {
                    this.hostStatus = "up";
                }
                String[] commandLines = origin.split("\n");
                boolean inPortAndService = false;
                for (String commandLine : commandLines) {
                    if (commandLine.contains("MAC Address:")) {
                        commandLine = commandLine.substring(13, 30);
                        this.macAddr = commandLine;
                        inPortAndService = false;
                    } else if (commandLine.contains("PORT") && commandLine.contains("STATE") &&
                            commandLine.contains("SERVICE")) {
                        inPortAndService = true;
                    } else if (inPortAndService) {
                        commandLine = commandLine.strip();
                        if (StringUtils.isNotEmpty(commandLine)) {
                            if (commandLine.charAt(0) > '9' || commandLine.charAt(0) < '0') {
                                inPortAndService = false;
                                continue;
                            }
                        }
                        String[] portAndServiceInfos = commandLine.split("\\s+");
                        if (portAndServiceInfos.length > 2) {
                            PortAndService portAndService = new PortAndService();
                            portAndService.setPort(portAndServiceInfos[0]);
                            portAndService.setState(portAndServiceInfos[1]);
                            portAndService.setService(portAndServiceInfos[2]);
                            this.portsAndServices.add(portAndService);
                        }
                    }
                }
            }

        }



    }

    @Data
    public static class SqlmapFormat {
        // 系统版本
        private String operatingSystem;
        // web应用
        private String webApplication;
        // 数据库信息
        private String dbms;

        public SqlmapFormat(String origin) {
            if (StringUtils.isNotEmpty(origin)) {
                String[] commandLines = origin.split("\n");
                for (String commandLine : commandLines) {
                    if (commandLine.contains("web server operating system:")) {
                        operatingSystem =  commandLine.substring(28, commandLine.length() - 1);
                    }
                    if (commandLine.contains("web application technology:")) {
                        webApplication =  commandLine.substring(27, commandLine.length() - 1);
                    }
                    if (commandLine.contains("back-end DBMS:")) {
                        dbms = commandLine.substring(14, commandLine.length() - 1);
                    }
                }
            }
        }

    }

    @Data
    public static class PortAndService {
        private String port;
        private String state;
        private String service;
    }
}


