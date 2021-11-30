package cn.cyberpeace.mp.cscene.dto.event;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 采集事件分类
 * @Author zhouxinghuai
 * @Date：2021/11/22 17:35
 */
@Data
public class EventAggr {

    @ApiModelProperty("进程百分比")
    private Double process;

    @ApiModelProperty("文件百分比")
    private Double file;

    @ApiModelProperty("端口百分比")
    private Double port;

    @ApiModelProperty("网络连接百分比")
    private Double network;

    @ApiModelProperty("用户百分比")
    private Double user;
}
