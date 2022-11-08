package cn.cyberpeace.mp.cscene.dto.event;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/3/12 17:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventTimeCount {

    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ApiModelProperty("总数")
    private Long count;
}
