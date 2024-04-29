package cn.cyberpeace.mp.base.bo;

import lombok.Data;

/**
 * @Description 系统配置信息表
 */
@Data
public class MpSysUrlConfig {

	/**
	 * 主键
	 */
	private Integer pk;

	/**
	 * key
	 */
	private String code;

	/**
	 * 服务地址
	 */
	private String serviceUrl;

	/**
	 * 创建时间
	 */
	private String createTime;

	/**
	 * 修改时间
	 */
	private String updateTime;
}
