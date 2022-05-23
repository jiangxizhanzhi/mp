package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * mp_situation_probe_ability
 * @author 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("mp_situation_probe_ability")
public class ProbeAbilityModel extends BaseModel {

    /**
     * 探针id
     */
    @ApiModelProperty(value = "探针id")
    private String probeId;

    /**
     * 功能名称
     */
    @ApiModelProperty(value = "功能名称")
    private String name;


    /**
     * 探针功能自己的操作系统，跟探针的不完全一样
     */
    @JsonIgnore
    private String osType;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

}
