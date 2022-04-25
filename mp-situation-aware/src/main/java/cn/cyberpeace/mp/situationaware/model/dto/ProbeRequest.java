package cn.cyberpeace.mp.situationaware.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("探针请求体")
public class ProbeRequest {

    @ApiModelProperty(value = "探针id,新增的时候不需要传")
    private String id;


    @ApiModelProperty(value = "探针名称")
    private String name;


    @ApiModelProperty(value = "描述")
    private String description;


    @ApiModelProperty(value = "0:信息类 1:事件类 2:日志类")
    private Integer probeCategory;


    @ApiModelProperty(value = "自定义探针输出格式: none, json, plain")
    private String outputFormat;


    @ApiModelProperty(value = "(可多选)操作系统类型 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other")
    private String osType;


    @ApiModelProperty(value = "探针脚本内容")
    private String scriptContent;

    @ApiModelProperty(value = "脚本参数 建议 ##包裹参数，方便替换")
    private List<ProbeParamsRequest> params;

}
