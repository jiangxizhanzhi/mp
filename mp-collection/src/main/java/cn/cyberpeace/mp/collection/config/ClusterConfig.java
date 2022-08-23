package cn.cyberpeace.mp.collection.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

public class ClusterConfig {
    public static List<Node> nodes = new ArrayList<>();

    private ClusterConfig() {
    }

    @Data
    public static class Node {
        private String nodeId;
        private String esAddress;
        private String wazuhUrl;
    }
}
