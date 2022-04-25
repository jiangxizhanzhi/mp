package cn.cyberpeace.mp.situationaware.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
  *@Desciption
  *@Author zhangjin
  *@Date 9/2/21 11:15 上午
**/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("探针脚本参数")
public class ProbeParamsRequest {

    /**
     * 参数名称
     */
    @ApiModelProperty(value = "参数名称")
    private String name;

    /**
     * 0:number 1:string 2:bool 3:ip
     */
    @ApiModelProperty(value = "0:number 1:string 2:bool 3:ip")
    private Integer type;

    /**
     * 默认值
     */
    @ApiModelProperty(value = "默认值")
    private String defaultValue;

    /**
     * 参数说明
     */
    @ApiModelProperty(value = "参数说明")
    private String description;

}
