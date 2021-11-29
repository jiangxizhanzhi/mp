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
 * @Description 流量脚本参数对象
 * @Author qiaokang
 * @Date 2021-11-25 15:00:45
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
