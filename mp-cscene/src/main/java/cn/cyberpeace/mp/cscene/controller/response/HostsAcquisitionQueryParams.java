package cn.cyberpeace.mp.cscene.controller.response;

import cn.cyberpeace.mp.cscene.request.element.ElementMonitorRequest;
import cn.cyberpeace.mp.cscene.request.logfile.LogFileSceneElementPageRequest;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostEventQueryParams;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author ling_yu
 * @Date 2025/1/2
 */
@Data
public class HostsAcquisitionQueryParams {

    @ApiModelProperty(value = "变更事件请求参数")
    private ElementMonitorRequest elementMonitorRequest;

    @ApiModelProperty(value = "原始日志请求参数")
    private LogFileSceneElementPageRequest logFileSceneElementPageRequest;

    @ApiModelProperty(value = "日志事件请求参数")
    private HostEventQueryParams hostEventQueryParams;

}
