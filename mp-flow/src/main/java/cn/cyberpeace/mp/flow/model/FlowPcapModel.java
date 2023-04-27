package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 流量包对象
 * @Author qiaokang
 * @Date 2023-04-27 09:21:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("mp_flow_pcap")
public class FlowPcapModel extends BaseModel {

	/**
	 * 流量id
	 */
	@ApiModelProperty(value = "流量id")
	@DefaultStr
	private String flowId;

	/**
	 * 时长(单位ms)
	 */
	@ApiModelProperty(value = "时长(单位ms)")
	private Long duration;

	/**
	 * 大小(单位Byte)
	 */
	@ApiModelProperty(value = "大小(单位Byte)")
	private Long pcapSize;

	/**
	 * 包数(报文数量)
	 */
	@ApiModelProperty(value = "包数(报文数量)")
	private Long pktNum;

	/**
	 * 包含协议
	 */
	@ApiModelProperty(value = "包含协议")
	@DefaultStr
	private String protocols;

	/**
	 * pcap文件地址
	 */
	@ApiModelProperty(value = "pcap文件地址")
	@DefaultStr
	private String pcapFile;


}
