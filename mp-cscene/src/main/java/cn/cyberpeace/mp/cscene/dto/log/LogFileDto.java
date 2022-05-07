package cn.cyberpeace.mp.cscene.dto.log;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/6 22:53
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LogFileDto extends LogFileBasicDto {

    @ApiModelProperty(value = "节点id")
    private String elementId;

    @ApiModelProperty("大小")
    private Long size;

    @ApiModelProperty("时间")
    private LocalDateTime time;

    private String downloadUrl;
}
