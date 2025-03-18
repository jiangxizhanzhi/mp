package cn.cyberpeace.mp.behavior.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class BehaviorBo implements Serializable {

	@ApiModelProperty("id")
	private String id;

	@ApiModelProperty("行为名称")
	private String name;

	@ApiModelProperty("参数配置")
	private String params;
}
