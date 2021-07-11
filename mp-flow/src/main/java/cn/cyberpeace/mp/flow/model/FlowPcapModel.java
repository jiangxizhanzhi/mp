package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("mp_flow_pcap")
public class FlowPcapModel extends BaseModel {

	
	@ApiModelProperty(value = "流量id")
	@DefaultStr
	private String flowId;

	
	@ApiModelProperty(value = "时长(单位ms)")
	private Long duration;

	
	@ApiModelProperty(value = "大小(单位Byte)")
	private Long pcapSize;

	
	@ApiModelProperty(value = "包数(报文数量)")
	private Long pktNum;

	
	@ApiModelProperty(value = "包含协议")
	@DefaultStr
	private String protocols;

	
	@ApiModelProperty(value = "pcap文件地址")
	@DefaultStr
	private String pcapFile;


}
