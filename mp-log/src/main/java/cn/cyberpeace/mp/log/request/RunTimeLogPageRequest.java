package cn.cyberpeace.mp.log.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class RunTimeLogPageRequest extends BasePageRequest {

    public static final Integer runTimeLogFlag = 1;

    public static final Integer securityLogFlag = 2;

    @ApiModelProperty(value = "日志类型 0:全部 1:运行日志 2:安全日志", required = false)
    private Integer runTimeLogType;

}
