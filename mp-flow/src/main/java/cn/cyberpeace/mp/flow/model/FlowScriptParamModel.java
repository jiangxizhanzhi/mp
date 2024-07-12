 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowScriptParamModel.java.
 // File size 1337.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName FlowScriptParamModel
 * @Description 流量脚本参数对象
 * @Author qiaokang
 * @Date 2021-11-25 15:00:45
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_script_param")
public class FlowScriptParamModel extends BaseModel {

	/**
	 * 流量id
	 */
	@ApiModelProperty(value = "流量id")
	private String flowId;

	/**
	 * 参数名
	 */
	@ApiModelProperty(value = "参数名")
	@DefaultStr
	private String paramKey;

	/**
	 * 参数值
	 */
	@ApiModelProperty(value = "参数值")
	@DefaultStr
	private String paramValue;

	/**
	 * 参数类型：0 string,1 IP,2 number,3 bool
	 */
	@ApiModelProperty(value = "参数类型：0 string,1 IP,2 number,3 bool")
	@DefaultInt
	private Integer paramType;

	/**
	 * 参数说明
	 */
	@ApiModelProperty(value = "参数说明")
	@DefaultStr
	private String paramDesc;


}
