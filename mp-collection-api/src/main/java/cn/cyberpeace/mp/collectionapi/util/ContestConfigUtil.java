package cn.cyberpeace.mp.collectionapi.util;

import cn.cyberpeace.mp.collectionapi.config.ContestConfig;
import cn.cyberpeace.mp.collectionapi.config.ContestConfig.Equipment;
import cn.hutool.core.collection.CollectionUtil;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

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
    
    /**
     * 根据设备类型得到对应的设备ip列表
     * 
     * @param type
     * @return
     */
    public static List<String> getIpsByType(String type, Integer useType) {
		if (CollectionUtil.isEmpty(ContestConfig.equipments)) {
			return Lists.newArrayList();
		}

		if (StringUtils.isBlank(type)) {
			return ContestConfig.equipments.stream().filter(e -> useType.equals(e.getUseType())).map(Equipment::getIps)
					.flatMap(Collection::stream).collect(Collectors.toList());
		} else {
			return ContestConfig.equipments.stream().filter(e -> type.equals(e.getType()) && useType.equals(e.getUseType()))
					.map(Equipment::getIps).flatMap(Collection::stream).collect(Collectors.toList());
		}
    }
    
    /**
     * 根据ip获取对应设备类型
     * 
     * @param ip
     * @return
     */
    public static String getTpyeByIp(String ip) {
    	if (CollectionUtil.isEmpty(ContestConfig.equipments)) {
			return null;
		}
    	
    	for(Equipment e : ContestConfig.equipments) {
    		if(e.getIps().contains(ip)) {
    			return e.getType();
    		}
    	}
    	
    	return null;
    }
    
}
