package cn.cyberpeace.mp.situationaware.model.dto.probe;

import cn.cyberpeace.mp.situationaware.model.ProbeModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
@ApiModel("探针信息")
public class ProbeResult extends ProbeModel {

    @ApiModelProperty(value = "探针脚本类型 0:shell 1:python 2:bat")
    private Integer scriptType;

    /**
     * 探针脚本内容
     */
    @ApiModelProperty(value = "探针脚本内容")
    // todo blob -> string?
    private String scriptContent;

    /**
     * 探针脚本内容fdfs地址
     */
    @ApiModelProperty(value = "探针脚本内容fdfs地址")
    private String scriptUrl;
}
