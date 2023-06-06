package cn.cyberpeace.mp.log.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @description 日志级别BO
 * @author wangssh
 * @date 2023/5/30
 * @version 1.0
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogLevelBO implements Serializable {

    private static final long serialVersionUID = 3260069067526609727L;

    /**
     * 默认全局模块ROOT，其他模块包名
     */
    @ApiModelProperty(value = "日志模块名")
    @NotBlank
    private String logName;

    @ApiModelProperty(value = "日志级别")
    private String logLevel;

}
