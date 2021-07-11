package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("mp_situation_probe_param")
public class ProbeParamModel extends BaseModel implements Serializable {

    
    @ApiModelProperty(value = "探针id")
    private String probeId;

    
    @ApiModelProperty(value = "采集模板id")
    @DefaultStr("")
    private String templateId;

    
    @ApiModelProperty(value = "参数名称")
    private String name;

    
    @ApiModelProperty(value = "0:number 1:string 2:bool 3:ip")
    private Integer type;

    
    @ApiModelProperty(value = "默认值")
    @DefaultStr("")
    private String defaultValue;

    
    @ApiModelProperty(value = "参数说明")
    @DefaultStr("")
    private String description;


}
