package cn.cyberpeace.mp.cscene.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName CommonCollectRes
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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
