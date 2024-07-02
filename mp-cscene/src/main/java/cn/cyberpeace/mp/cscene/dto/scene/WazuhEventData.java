package cn.cyberpeace.mp.cscene.dto.scene;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @ClassName WazuhEventData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
public class WazuhEventData {

    private Agent agent;

    private Rule rule;

    private String fullLog;

    private Date timestamp;

    @Data
    @NoArgsConstructor
    public static class Agent {
        private String ip;
        private String name;
    }

    @Data
    @NoArgsConstructor
    public static class Rule {
        private RuleMitre mitre;
        private List<String> groups;
    }

    @Data
    @NoArgsConstructor
    public static class RuleMitre {
        private List<String> technique;
        private List<String> id;
        private List<String> tactic;
    }
}
