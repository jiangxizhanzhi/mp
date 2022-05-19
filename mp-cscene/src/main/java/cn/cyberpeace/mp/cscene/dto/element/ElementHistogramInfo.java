package cn.cyberpeace.mp.cscene.dto.element;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ElementHistogramInfo {
    @ApiModelProperty(value = "时间点")
    private String time;
    @ApiModelProperty(value = "变更数量")
    private Integer count;
}
