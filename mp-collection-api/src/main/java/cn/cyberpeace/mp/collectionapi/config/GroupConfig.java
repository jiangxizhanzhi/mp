package cn.cyberpeace.mp.collectionapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "mp.situationaware.team")
@Data
public class GroupConfig {
    private List<Group> groups;

    @Data
    public static class Group {
        private String groupId;
        private String esNode;
        private String wazuhUrl;
        private List<Node> nodes;

        @Data
        public static class Node {
            private String nodeIp;
            private List<String> nodeNames;
            private List<String> targetIps;
        }
    }
}
