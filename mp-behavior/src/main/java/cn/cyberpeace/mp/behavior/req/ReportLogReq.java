package cn.cyberpeace.mp.behavior.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
public class ReportLogReq implements Serializable {

	@ApiModelProperty("日志文件")
	private MultipartFile log;

	@ApiModelProperty("运行行为id")
	private String behaviorId;

}
