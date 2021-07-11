package cn.cyberpeace.mp.cscene.dto.event;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventTimeCount {

    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ApiModelProperty("总数")
    private Long count;
}
