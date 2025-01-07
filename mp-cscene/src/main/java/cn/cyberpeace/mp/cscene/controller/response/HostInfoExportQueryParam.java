package cn.cyberpeace.mp.cscene.controller.response;

import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author ling_yu
 * @Date 2025/1/2
 */
@Data
public class HostInfoExportQueryParam {

    @ApiModelProperty("入参")
    private TimeListPageInfoQueryParams hostInfoQueryParams;

    @ApiModelProperty("网卡信息入参")
    private TimeListPageInfoQueryParams netifacesInfoQueryParams;;

    @ApiModelProperty("端口信息入参")
    private TimeListPageInfoQueryParams portsInfoQueryParams;

    @ApiModelProperty("进程信息入参")
    private TimeListPageInfoQueryParams processesInfoQueryParams;

    @ApiModelProperty("主机名")
    private String hostName;
}
