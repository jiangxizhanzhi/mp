package cn.cyberpeace.mp.devopscluster.dto;

import cn.cyberpeace.mp.base.bo.AttackType;
import lombok.Data;

@Data
public class AttackToolDto {
    private String toolName;
    private String toolId;


    public AttackToolDto(AttackType attackType) {
        this.toolName = attackType.getAttackDesc();
        this.toolId = attackType.getToolId();
    }
}
