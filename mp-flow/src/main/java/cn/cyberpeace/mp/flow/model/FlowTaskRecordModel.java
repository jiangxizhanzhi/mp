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
@Table("mp_flow_task_record")
public class FlowTaskRecordModel extends BaseModel {

	
	@ApiModelProperty(value = "流量执行器id")
	@DefaultStr
	private String actuatorId;

	
	@ApiModelProperty(value = "任务id")
	@DefaultStr
	private String taskId;

	
	@ApiModelProperty(value = "实时发送速率(单位byte/s)")
	private Long txBps;

	
	@ApiModelProperty(value = "实时发包数")
	private Long txPps;

	
	@ApiModelProperty(value = "延迟(单位ms)")
	private Long latency;

	
	@ApiModelProperty(value = "抖动(单位ms)")
	private Long jitter;

	
	@ApiModelProperty(value = "发包总大小(单位byte)")
	private Long obytes;

	
	@ApiModelProperty(value = "发包错误数")
	private Long oerrors;

	
	@ApiModelProperty(value = "发送速率L2(单位byte/s)")
	private Long totalTxL2;

	
	@ApiModelProperty(value = "发送速率L1(单位byte/s)")
	private Long totalTxL1;

	
	@ApiModelProperty(value = "总发包数")
	private Long totalPps;

	
	@ApiModelProperty(value = "接收速率(单位byte/s)")
	private Long totalRx;

	
	@ApiModelProperty(value = "阻塞队列大小(单位byte)")
	private Long queueFull;

	
	@ApiModelProperty(value = "丢包率(单位byte/s)")
	private Long dropRate;

	
	@ApiModelProperty(value = "是否有效：0 无效，1 有效")
	private Integer recordStatus;


}
