package cn.cyberpeace.mp.situationaware.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("探针参数")
public class ProbeParamsResult {

    @ApiModelProperty(value = "探针参数id")
    private String id;

    @ApiModelProperty(value = "探针id")
    private String probeId;

    @ApiModelProperty(value = "参数类型 0:number 1:string 2:bool 3:ip")
    private Integer paramType;

    @ApiModelProperty(value = "参数类型默认值")
    private String defaultVal;

    @ApiModelProperty(value = "参数描述")
    private String paramDes;

    @ApiModelProperty(value = "参数创建时间")
    private Date createTime;

    @ApiModelProperty(value = "参数修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "参数名称")
    private String paramName;
}
