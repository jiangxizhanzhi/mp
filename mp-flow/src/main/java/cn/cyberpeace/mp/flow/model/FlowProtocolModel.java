package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_protocol")
public class FlowProtocolModel extends BaseModel {

	
	@ApiModelProperty(value = "流量id")
	@DefaultStr
	private String flowId;

	
	@ApiModelProperty(value = "协议名称", required = true)
	@DefaultStr
	private String protocolName;

	
	@ApiModelProperty(value = "比例", required = true)
	private BigDecimal rate;

	
	@ApiModelProperty(value = "是否勾选：0 否，1 是", required = true)
	private Integer marked;


}
