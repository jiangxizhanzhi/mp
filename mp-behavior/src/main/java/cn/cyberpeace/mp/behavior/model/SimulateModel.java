package cn.cyberpeace.mp.behavior.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("mp_behavior_simulate")
public class SimulateModel extends BaseModel {

	@ApiModelProperty("场景id")
	private String sceneId;

	@ApiModelProperty("场景名称(实验任务名称)")
	private String sceneName;

	@ApiModelProperty("节点id")
	private String nodeId;

	@ApiModelProperty("节点名称(子仪表名称)")
	private String nodeName;

	@ApiModelProperty("节点ip")
	private String ip;

	@ApiModelProperty("状态")
	private Integer status;

	@ApiModelProperty("是否删除")
	private Boolean delete;

	@ApiModelProperty("开始时间")
	private LocalTime startTime;

	@ApiModelProperty("结束时间")
	private LocalTime endTime;

	@ApiModelProperty("在线")
	private Boolean online;

	public enum StatusEnum{
		NOT_STARTED(0, "未开始"),
		RUNNING(1, "执行中"),
		PAUSED(2, "暂停中"),
		STOPPED(3, "已停止");

		@Getter
		private Integer code;

		@Getter
		private String desc;

		StatusEnum(Integer code, String desc) {
			this.code = code;
			this.desc = desc;
		}
	}
}
