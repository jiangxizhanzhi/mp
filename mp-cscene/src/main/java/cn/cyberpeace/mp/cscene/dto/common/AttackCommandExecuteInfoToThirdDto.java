package cn.cyberpeace.mp.cscene.dto.common;

import cn.cyberpeace.mp.base.bo.AttackType;
import cn.cyberpeace.mp.base.common.JsonCommon;
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
        this.category = attackType.getCategoryId();
        this.attackDesc = attackType.getAttackDesc();
        this.attackEffectedFlag = attackCommandHistoryModel.getAttackEffectedFlag();
        this.attackCommandOutput = new AttackCommandOutput(attackCommandHistoryModel.getAttackCommandOutput());
    }


    @Data
    public static class AttackCommandOutput {
        private String origin;
        private String format;


        public AttackCommandOutput(String origin) {
            this.origin = origin;
            if (StringUtils.isNotEmpty(origin) && origin.contains("Nmap")) {
                this.format = JsonCommon.stringify(new NmapFormat(origin));
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
    public static class PortAndService {
        private String port;
        private String state;
        private String service;
    }
}


