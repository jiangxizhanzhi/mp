package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 流量任务记录对象
 * @Author qiaokang
 * @Date 2023-04-27 09:21:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_task_record")
public class FlowTaskRecordModel extends BaseModel {

	/**
	 * 流量执行器id
	 */
	@ApiModelProperty(value = "流量执行器id")
	@DefaultStr
	private String actuatorId;

	/**
	 * 任务id
	 */
	@ApiModelProperty(value = "任务id")
	@DefaultStr
	private String taskId;

	/**
	 * 实时发送速率(单位byte/s)
	 */
	@ApiModelProperty(value = "实时发送速率(单位byte/s)")
	private Long txBps;

	/**
	 * 实时发包数
	 */
	@ApiModelProperty(value = "实时发包数")
	private Long txPps;

	/**
	 * 延迟(单位ms)
	 */
	@ApiModelProperty(value = "延迟(单位ms)")
	private Long latency;

	/**
	 * 抖动(单位ms)
	 */
	@ApiModelProperty(value = "抖动(单位ms)")
	private Long jitter;

	/**
	 * 发包总大小(单位byte)
	 */
	@ApiModelProperty(value = "发包总大小(单位byte)")
	private Long obytes;

	/**
	 * 发包错误数
	 */
	@ApiModelProperty(value = "发包错误数")
	private Long oerrors;

	/**
	 * 发送速率L2(单位byte/s)
	 */
	@ApiModelProperty(value = "发送速率L2(单位byte/s)")
	private Long totalTxL2;

	/**
	 * 发送速率L1(单位byte/s)
	 */
	@ApiModelProperty(value = "发送速率L1(单位byte/s)")
	private Long totalTxL1;

	/**
	 * 总发包数
	 */
	@ApiModelProperty(value = "总发包数")
	private Long totalPps;

	/**
	 * 接收速率(单位byte/s)
	 */
	@ApiModelProperty(value = "接收速率(单位byte/s)")
	private Long totalRx;

	/**
	 * 阻塞队列大小(单位byte)
	 */
	@ApiModelProperty(value = "阻塞队列大小(单位byte)")
	private Long queueFull;

	/**
	 * 丢包率(单位byte/s)
	 */
	@ApiModelProperty(value = "丢包率(单位byte/s)")
	private Long dropRate;

	/**
	 * 是否有效：0 无效，1 有效
	 */
	@ApiModelProperty(value = "是否有效：0 无效，1 有效")
	private Integer recordStatus;


}
