package cn.cyberpeace.mp.behavior.res;

import cn.cyberpeace.mp.behavior.bo.Param;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BehaviorListRes implements Serializable {

	@ApiModelProperty("id")
	private String id;

	@ApiModelProperty("行为名称")
	private String name;

	@ApiModelProperty("参数配置")
	private List<Param> paramList;
}
