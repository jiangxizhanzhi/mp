package cn.cyberpeace.mp.collectionapi.config;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class ContestConfig {
    private ContestConfig() {

    }

    public static List<Team> teams;

    public static List<Equipment> equipments;

    @Data
    public static class Team {
        private String name;

        private String ip;
    }

    @Data
    public static class Equipment {
        private String name;

        private String type;

        private String boxType;

        private List<String> ips;
    }
}
