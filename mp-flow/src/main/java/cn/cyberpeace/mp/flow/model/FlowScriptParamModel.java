package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_script_param")
public class FlowScriptParamModel extends BaseModel {

	
	@ApiModelProperty(value = "流量id")
	private String flowId;

	
	@ApiModelProperty(value = "参数名")
	@DefaultStr
	private String paramKey;

	
	@ApiModelProperty(value = "参数值")
	@DefaultStr
	private String paramValue;

	
	@ApiModelProperty(value = "参数类型：0 string,1 IP,2 number,3 bool")
	@DefaultInt
	private Integer paramType;

	
	@ApiModelProperty(value = "参数说明")
	@DefaultStr
	private String paramDesc;


}
