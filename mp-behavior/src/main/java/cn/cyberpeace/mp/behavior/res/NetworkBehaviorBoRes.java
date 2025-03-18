package cn.cyberpeace.mp.behavior.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalTime;

@Data
public class NetworkBehaviorBoRes implements Serializable {

	@ApiModelProperty("id")
	private String id;

	@ApiModelProperty("名称")
	private String name;

	@ApiModelProperty("分类")
	private Integer type;

	@ApiModelProperty("步骤")
	private Integer step;

	@ApiModelProperty("运行时间")
	private String runTime;
}
