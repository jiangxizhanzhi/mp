package cn.cyberpeace.mp.base.bo;

import lombok.Data;

import java.util.List;

/**
 * @Description 系统码表
 */
@Data
public class MpSysCodeMenu {

	/**
	 * 主键
	 */
	private Integer pk;

	/**
	 * code编码
	 */
	private String code;

	/**
	 * 名称
	 */
	private String codeName;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 类型
	 */
	private String type;

	/**
	 * 补充
	 */
	private String supplement;

	/**
	 * 父级code
	 */
	private String parentCode;

	/**
	 * 层级
	 */
	private Integer level;

	/**
	 * 状态(1:使用 2:禁用)
	 */
	private Integer status;

	/**
	 * 创建人
	 */
	private String creator;

	/**
	 * 创建时间
	 */
	private String createTime;

	/**
	 * 子集
	 */
	private List<MpSysCodeMenu> children;
}
