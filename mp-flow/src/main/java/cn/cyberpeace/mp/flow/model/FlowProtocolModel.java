package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 流量协议对象
 * @Author qiaokang
 * @Date 2023-04-27 09:22:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_protocol")
public class FlowProtocolModel extends BaseModel {

	/**
	 * 流量id
	 */
	@ApiModelProperty(value = "流量id")
	@DefaultStr
	private String flowId;

	/**
	 * 协议名称
	 */
	@ApiModelProperty(value = "协议名称", required = true)
	@DefaultStr
	private String protocolName;

	/**
	 * 比例
	 */
	@ApiModelProperty(value = "比例", required = true)
	private Float rate;

	/**
	 * 是否勾选：0 否，1 是
	 */
	@ApiModelProperty(value = "是否勾选：0 否，1 是", required = true)
	private Integer marked;


}
