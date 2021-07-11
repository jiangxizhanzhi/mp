package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SceneInfoVO extends BaseVO {

    
    @ApiModelProperty("场景名称")
    private String sceneName;


    
    @ApiModelProperty(value = "引用类型")
    private Integer referenceType;


}
