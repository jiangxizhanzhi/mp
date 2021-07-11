package cn.cyberpeace.mp.log.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogLevelBO implements Serializable {

    private static final long serialVersionUID = 3260069067526609727L;

    
    @ApiModelProperty(value = "日志模块名")
    @NotBlank
    private String logName;

    @ApiModelProperty(value = "日志级别")
    private String logLevel;

}
