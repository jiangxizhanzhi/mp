package cn.cyberpeace.mp.collectionapi.request;

import lombok.Data;

import java.util.List;

@Data
public class AttackFlowBaseRequest {
    private List<Aggregate> sources;

    private List<Aggregate> targets;

    @Data
    public static class Aggregate {
        private String name;

        private List<String> ips;

        private String cidr;
    }
}
