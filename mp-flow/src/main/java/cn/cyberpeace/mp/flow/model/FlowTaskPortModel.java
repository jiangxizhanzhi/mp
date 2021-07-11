package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_task_port")
public class FlowTaskPortModel extends BaseModel {

	
	@ApiModelProperty(value = "网络id")
	@DefaultStr
	private String networkId;

	
	@ApiModelProperty(value = "流量执行器id")
	@DefaultStr
	private String actuatorId;

	
	@ApiModelProperty(value = "网口mac")
	@DefaultStr
	private String mac;


}
