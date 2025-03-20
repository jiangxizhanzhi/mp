package cn.cyberpeace.mp.behavior.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("mp_behavior_task")
public class TaskModel extends BaseModel {

	@ApiModelProperty("仪表id")
	private String simulateId;

	@ApiModelProperty("行为模板id")
	private String networkBehaviorId;

	@ApiModelProperty("行为名称")
	private String name;

	@ApiModelProperty("行为描述")
	private String description;

	@ApiModelProperty("文件下载地址")
	private String fileUrl;

	@ApiModelProperty("参数配置")
	private String params;

	@ApiModelProperty("目标服务")
	private String target;

	@ApiModelProperty("分类")
	private Integer type;

	@ApiModelProperty("步骤")
	private Integer step;

	@ApiModelProperty("运行时间")
	private LocalTime runTime;

	@ApiModelProperty("是否删除")
	private Boolean delete;
}
