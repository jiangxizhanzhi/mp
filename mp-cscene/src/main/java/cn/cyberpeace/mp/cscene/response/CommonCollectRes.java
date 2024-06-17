package cn.cyberpeace.mp.cscene.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommonCollectRes {

	@ApiModelProperty("主机名称")
	private String hostName;

	@ApiModelProperty("采集协议")
	private String agreement;

	@ApiModelProperty("采集时间")
	private LocalDateTime time;

	@ApiModelProperty("详情")
	private String detail;
}
