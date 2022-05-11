package cn.cyberpeace.mp.situationaware.model.dto.probe;

import cn.cyberpeace.mp.situationaware.model.dto.validate.AddGroup;
import cn.cyberpeace.mp.situationaware.model.dto.validate.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("探针请求体")
public class ProbeRequest {

    @ApiModelProperty(value = "探针id,新增的时候不需要传")
    @NotBlank(groups = {UpdateGroup.class})
    private String id;


    @ApiModelProperty(value = "探针名称")
    @NotBlank(groups = {AddGroup.class})
    private String name;


    @ApiModelProperty(value = "描述")
    private String description;


    @ApiModelProperty(value = "0:信息类 1:事件类 2:日志类")
    private Integer probeCategory;


    @ApiModelProperty(value = "自定义探针输出格式: none, json, plain")
    private String outputFormat;


    @ApiModelProperty(value = "分析引擎")
    private String engine;


    @ApiModelProperty(value = "集合类型，操作系统类型 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other")
    private List<Integer> osTypes;

    /**
     * 0:shell 1:python 2:bat
     */
    @ApiModelProperty(value = "0:shell 1:python 2:bat")
    @NotNull(groups = {AddGroup.class})
    private Integer scriptType;

    @ApiModelProperty(value = "探针脚本内容")
    @NotBlank(groups = {AddGroup.class})
    private String scriptContent;

    @ApiModelProperty(value = "探针脚本Url")
    private String scriptUrl;

    @ApiModelProperty(hidden = true)
    private String scriptChecksum;

    @ApiModelProperty(value = "脚本参数列表")
    private List<ProbeParamsRequest> params;

}
