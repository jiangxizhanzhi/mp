package cn.cyberpeace.mp.collection.bo;

import java.util.List;

import lombok.Data;

/**
 * @Description: 
 * @author zhangjiaxin
 * @date 2022年10月25日下午4:23:06 
 */

@Data
public class EsIpFieldBO {

	private Field field;
	private FilterType filterType;
	private List<String> ips;
	
	public enum FilterType {
		LIST, PREFIX;
	}
	
	public enum Field {
		SOURCE, DESTINATION;
	}
	
	public String getIPFieldName() {
		return field.name().toLowerCase() + ".ip";
	}
}
