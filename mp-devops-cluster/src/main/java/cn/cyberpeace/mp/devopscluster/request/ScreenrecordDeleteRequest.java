package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@ApiModel("按时间删除录屏请求")
public class ScreenrecordDeleteRequest {

    
    @ApiModelProperty("删除条件：1: 6个月之前，2：3个月之前")
    @NotNull(message = "recordTime can not be empty")
    private Integer recordTime;

}
