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

/**
 * mp_situation_probe_param
 * @author 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("mp_situation_probe_param")
public class ProbeParamModel extends BaseModel implements Serializable {

    /**
     * 探针id
     */
    @ApiModelProperty(value = "探针id")
    private String probeId;

    /**
     * 采集模板id
     */
    @ApiModelProperty(value = "采集模板id")
    @DefaultStr("")
    private String templateId;

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
    @DefaultStr("")
    private String defaultValue;

    /**
     * 参数说明
     */
    @ApiModelProperty(value = "参数说明")
    @DefaultStr("")
    private String description;


}
