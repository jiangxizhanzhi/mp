package cn.cyberpeace.mp.cscene.dto.element;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElementHistogramInfo {
    @ApiModelProperty(value = "时间点")
    private String time;
    @ApiModelProperty(value = "变更数量")
    private Integer count;
    @JsonIgnore
    @ApiModelProperty(value = "时间点")
    private LocalDateTime localDateTime;
}
