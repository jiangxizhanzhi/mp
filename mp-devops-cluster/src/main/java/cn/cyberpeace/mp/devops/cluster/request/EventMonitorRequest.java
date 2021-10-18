package cn.cyberpeace.mp.devops.cluster.request;

import cn.cyberpeace.mp.base.constant.BaseConstants;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @Description: 事件监控请求
 * @Author qiaokang
 * @Date：2021/7/9 09:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("事件监控请求")
public class EventMonitorRequest extends BasePageRequest {

    @ApiModelProperty(value = "场景id", required = true)
    @NotEmpty(message = "场景id不能为空")
    private String sceneId;

    @ApiModelProperty(value = "开始时间", required = true)
    @NotEmpty(message = "开始时间不能为空")
    private String startTime;

    @ApiModelProperty(value = "结束时间", required = true)
    @NotEmpty(message = "结束时间不能为空")
    private String endTime;

    /**
     * 获取参数url拼接
     * @return
     */
    public String getUrlParam() {
        StringBuffer urlBuffer = new StringBuffer();
        urlBuffer.append(BaseConstants.CONNECTOR)
                .append("start_time")
                .append(BaseConstants.EQUAL)
                .append(this.startTime)
                .append(BaseConstants.CONNECTOR)
                .append("end_time")
                .append(BaseConstants.EQUAL)
                .append(this.endTime)
                .append(BaseConstants.CONNECTOR)
                .append(ControllerConstant.STR_PER_PAGE)
                .append(BaseConstants.EQUAL)
                .append(this.getPerPage());
        return urlBuffer.toString();
    }

}
