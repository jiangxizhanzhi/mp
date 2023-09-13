package cn.cyberpeace.mp.situationaware.constant;

import cn.cyberpeace.mp.base.common.MessageCommon;

/**
 * metri tactic 枚举类
 * @author zhanzhi
 */
public enum MetriTacticEnum {

    RECONNAISSANCE("Reconnaissance", SituationBase18nCode.RECONNAISSANCE),
    RESOURCE_DEVELOPMENT("Resource Development", SituationBase18nCode.RESOURCE_DEVELOPMENT),
    INITIAL_ACCESS("Initial Access", SituationBase18nCode.INITIAL_ACCESS),
    EXECUTION("Execution", SituationBase18nCode.EXECUTION),
    PERSISTENCE("Persistence", SituationBase18nCode.PERSISTENCE),
    PRIVILEGE_ESCALATION("Privilege Escalation", SituationBase18nCode.PRIVILEGE_ESCALATION),
    DEFENSE_EVASION("Defense Evasion", SituationBase18nCode.DEFENSE_EVASION),
    CREDENTIAL_ACCESS("Credential Access", SituationBase18nCode.CREDENTIAL_ACCESS),
    DISCOVERY("Discovery", SituationBase18nCode.DISCOVERY),
    LATERAL_MOVEMENT("Lateral Movement", SituationBase18nCode.LATERAL_MOVEMENT),
    COLLECTION("Collection", SituationBase18nCode.COLLECTION),
    COMMAND_AND_CONTROL("Command And Control", SituationBase18nCode.COMMAND_AND_CONTROL),
    EXFILTRATION("Exfiltration", SituationBase18nCode.EXFILTRATION),
    IMPACT("Impact", SituationBase18nCode.IMPACT),

    NOT_VALID("", "");


    ;


    MetriTacticEnum(String tactic, String i18nCode) {
        this.tactic = tactic;
        this.i18nCode = i18nCode;
    }
    private final String tactic;

    private final String i18nCode;


    public String getTactic() {
        return tactic;
    }

    private String getI18nCode() {
        return i18nCode;
    }

    public String getI18nMessage() {
        return MessageCommon.get(this.getI18nCode());
    }

    public static MetriTacticEnum getByTactic(String tactic) {
        for (MetriTacticEnum value : MetriTacticEnum.values()) {
            if (value.getTactic().equals(tactic)) {
                return value;
            }
        }
        return NOT_VALID;
    }
}
