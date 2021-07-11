package cn.cyberpeace.mp.situationaware.model.dto.probe;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("探针参数请求体")
public class ProbeParamRequest {

    
    @ApiModelProperty(value = "参数名称")
    private String name;

    
    @ApiModelProperty(value = "0:number 1:string 2:bool 3:ip")
    private Integer type;

    
    @ApiModelProperty(value = "默认值")
    private String defaultValue;

    
    @ApiModelProperty(value = "参数说明")
    private String description;


}
