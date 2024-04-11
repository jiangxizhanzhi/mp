package cn.cyberpeace.mp.devopscluster.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @Description 系统码表
 */
@Data
public class MpSysCodeEnum {

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
	private List<MpSysCodeEnum> children;
}
