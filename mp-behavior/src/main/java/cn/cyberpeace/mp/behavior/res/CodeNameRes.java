package cn.cyberpeace.mp.behavior.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeNameRes {

	@ApiModelProperty("code值")
	private Integer code;

	@ApiModelProperty("名称")
	private String name;
}
