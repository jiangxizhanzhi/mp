package cn.cyberpeace.mp.cscene.dto.scene;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

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
    }

    @Data
    @NoArgsConstructor
    public static class RuleMitre {
        private List<String> technique;
        private List<String> id;
        private List<String> tactic;
    }
}
