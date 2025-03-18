package cn.cyberpeace.mp.behavior.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DeleteReq implements Serializable {

	@ApiModelProperty("id列表")
	private List<String> ids;
}
