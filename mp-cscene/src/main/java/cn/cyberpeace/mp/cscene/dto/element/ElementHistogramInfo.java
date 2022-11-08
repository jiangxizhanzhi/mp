package cn.cyberpeace.mp.cscene.dto.element;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ElementHistogramInfo {
    @ApiModelProperty(value = "时间点")
    private String time;
    @ApiModelProperty(value = "变更数量")
    private Integer count;
    @JsonIgnore
    @ApiModelProperty(value = "时间点")
    private LocalDateTime localDateTime;
}
