package cn.cyberpeace.mp.collectionapi.util;

import cn.cyberpeace.mp.collectionapi.config.ContestConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ContestConfigUtil {
    private ContestConfigUtil() {

    }

    private static Map<String, String> ip2Team;

    private static Map<String, String> ip2Equipment;

    public static Map<String, String> getIp2Team() {
        if (Objects.isNull(ip2Team)) {
            ip2Team = ContestConfig.teams.stream().collect(Collectors.toMap(team -> team.getIp(), team -> team.getName()));
        }
        return ip2Team;
    }

    public static Map<String, String> getIp2Equipment() {
        if (Objects.isNull(ip2Equipment)) {
            ip2Equipment = new HashMap<>();
            ContestConfig.equipments.forEach(equipment -> equipment.getIps().forEach(ip -> ip2Equipment.put(ip, equipment.getName())));
        }
        return ip2Equipment;
    }
}
