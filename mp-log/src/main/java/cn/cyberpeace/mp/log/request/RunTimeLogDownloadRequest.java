package cn.cyberpeace.mp.log.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class RunTimeLogDownloadRequest {

    @ApiModelProperty(value = "日志上报类型")
    private String type;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "节点名称")
    private String hostname;

    @ApiModelProperty(value = "路径")
    private String path;
}
