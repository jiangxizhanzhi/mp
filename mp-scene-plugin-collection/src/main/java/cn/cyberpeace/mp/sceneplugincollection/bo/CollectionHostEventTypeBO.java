package cn.cyberpeace.mp.sceneplugincollection.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionHostEventTypeBO {

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("value")
    private Integer value;
}
