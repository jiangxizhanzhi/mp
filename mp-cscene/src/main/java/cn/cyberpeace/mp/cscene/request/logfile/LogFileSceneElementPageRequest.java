package cn.cyberpeace.mp.cscene.request.logfile;

import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class LogFileSceneElementPageRequest extends BaseSceneElementPageRequest {
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
