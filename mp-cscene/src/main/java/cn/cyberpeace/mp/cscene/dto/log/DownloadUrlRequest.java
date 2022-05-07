package cn.cyberpeace.mp.cscene.dto.log;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/7 10:04
 */
@Data
public class DownloadUrlRequest {

    @ApiModelProperty(value = "请求个体")
    private List<LogFileBasicDto> items;

}
