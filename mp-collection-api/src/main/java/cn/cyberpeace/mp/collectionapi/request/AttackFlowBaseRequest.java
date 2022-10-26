package cn.cyberpeace.mp.collectionapi.request;

import lombok.Data;

import java.util.List;

@Data
public class AttackFlowBaseRequest {
    private List<IPField> sources;

    private List<IPField> targets;

    @Data
    public static class IPField {
        private String name;

        private List<String> ips;

        private String cidr;
    }
}
