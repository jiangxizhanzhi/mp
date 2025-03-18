package cn.cyberpeace.mp.behavior.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Param implements Serializable {

	@ApiModelProperty("参数")
	private String param;

	@ApiModelProperty("参数值")
	private String value;

	@ApiModelProperty("描述")
	private String desc;
}
