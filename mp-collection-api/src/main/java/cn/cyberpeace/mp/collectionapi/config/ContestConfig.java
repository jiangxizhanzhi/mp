package cn.cyberpeace.mp.collectionapi.config;

import lombok.Data;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import cn.hutool.core.collection.CollectionUtil;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

        // 0:商用 1:拟态
        private Integer useType;

        private List<String> ips;
    }
    
    /**
     * 根据设备类型得到对应的设备ip列表
     * 
     * @param type
     * @return
     */
    public static List<String> getIpsByType(String type, Integer useType) {
		if (CollectionUtil.isEmpty(equipments)) {
			return Lists.newArrayList();
		}

		if (StringUtils.isBlank(type)) {
			return equipments.stream().filter(e -> useType.equals(e.getUseType())).map(Equipment::getIps)
					.flatMap(Collection::stream).collect(Collectors.toList());
		} else {
			return equipments.stream().filter(e -> type.equals(e.getType()) && useType.equals(e.getUseType()))
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
    	if (CollectionUtil.isEmpty(equipments)) {
			return null;
		}
    	
    	for(Equipment e : equipments) {
    		if(e.getIps().contains(ip)) {
    			return e.getType();
    		}
    	}
    	
    	return null;
    }
    
}
