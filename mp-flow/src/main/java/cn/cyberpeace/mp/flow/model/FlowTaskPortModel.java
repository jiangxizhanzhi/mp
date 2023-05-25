package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 流量任务网口对象
 * @Author qiaokang
 * @Date 2023-04-27 09:21:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_task_port")
public class FlowTaskPortModel extends BaseModel {

	/**
	 * 网络id
	 */
	@ApiModelProperty(value = "网络id")
	@DefaultStr
	private String networkId;

	/**
	 * 流量执行器id
	 */
	@ApiModelProperty(value = "流量执行器id")
	@DefaultStr
	private String actuatorId;

	/**
	 * 网口mac
	 */
	@ApiModelProperty(value = "网口mac")
	@DefaultStr
	private String mac;


}
