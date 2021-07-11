package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("mp_situation_probe_ability")
public class ProbeAbilityModel extends BaseModel {

    
    @ApiModelProperty(value = "探针id")
    private String probeId;

    
    @ApiModelProperty(value = "功能名称")
    private String name;


    
    @ApiModelProperty(value = "分析引擎 Y/N")
    private String engine;


    
    @JsonIgnore
    private String osType;

    
    @ApiModelProperty(value = "描述")
    private String description;

}
