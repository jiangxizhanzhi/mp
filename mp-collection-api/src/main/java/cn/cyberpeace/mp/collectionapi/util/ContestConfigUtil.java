package cn.cyberpeace.mp.collectionapi.util;

import cn.cyberpeace.mp.collectionapi.config.ContestConfig;
import cn.cyberpeace.mp.collectionapi.config.ContestConfig.Equipment;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ContestConfigUtil {
    private ContestConfigUtil() {

    }

    private static Map<String, ContestConfig.Team> ip2Team;

    private static Map<String, Equipment> ip2Equipment;

	public static Map<String, ContestConfig.Team> getIp2Team() {
		if (Objects.isNull(ip2Team)) {
			ip2Team = ContestConfig.teams.stream().collect(Collectors.toMap(team -> team.getIp(), team -> team));
		}
		return ip2Team;
	}

	public static Map<String, Equipment> getIp2Equipment() {
		if (Objects.isNull(ip2Equipment)) {
			ip2Equipment = new HashMap<>();
			ContestConfig.equipments.forEach(equipment -> equipment.getIps().forEach(ip -> ip2Equipment.put(ip, equipment)));
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
		if (CollectionUtils.isEmpty(ContestConfig.equipments)) {
			return Lists.newArrayList();
		}

		return ContestConfig.equipments.stream().filter(e -> {
			if (StringUtils.isNotBlank(type) && !type.equals(e.getType())) {
				return false;
			}
			if (useType != null && !useType.equals(e.getUseType())) {
				return false;
			}
			return true;
		}).map(Equipment::getIps).flatMap(Collection::stream).collect(Collectors.toList());
    }
    
    /**
     * 根据ip获取对应设备类型
     * 
     * @param ip
     * @return
     */
    public static String getTpyeByIp(String ip) {
    	if (CollectionUtils.isEmpty(ContestConfig.equipments)) {
			return null;
		}
    	
    	for(Equipment e : ContestConfig.equipments) {
    		if(e.getIps().contains(ip)) {
    			return e.getType();
    		}
    	}
    	
    	return null;
    }
    
    /**
     * 根据ip获取对应设备名称
     * 
     * @param ip
     * @return
     */
    public static String getNameByIp(String ip) {
    	if (CollectionUtils.isEmpty(ContestConfig.equipments)) {
			return null;
		}
    	
    	for(Equipment e : ContestConfig.equipments) {
    		if(e.getIps().contains(ip)) {
    			return e.getName();
    		}
    	}
    	
    	return null;
    }
    
}
