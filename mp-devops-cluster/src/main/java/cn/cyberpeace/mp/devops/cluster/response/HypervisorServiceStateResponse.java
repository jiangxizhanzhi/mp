package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.devops.cluster.enums.CloudPlatformEnum;
import cn.cyberpeace.mp.devops.cluster.enums.FontColorEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务状态信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "服务状态信息")
public class HypervisorServiceStateResponse extends BaseCloudPlatformResponse {

    /**
     * 服务
     */
    @ApiModelProperty(value = "服务")
    private String name;

    /**
     * 是否存活
     */
    @ApiModelProperty(value = "是否存活")
    @JsonIgnore
    private Boolean alive;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private String state;

    /**
     * 字体颜色
     */
    @ApiModelProperty(value = "字体颜色")
    private FontColorEnum fontColor;

    public HypervisorServiceStateResponse(String name, Boolean alive) {
        this.name = name;
        this.alive = alive;
    }

    public HypervisorServiceStateResponse(String name, String state) {
        this.name = name;
        this.state = state;
    }

    public HypervisorServiceStateResponse(String name, Boolean alive, String state) {
        this.name = name;
        this.alive = alive;
        this.state = state;
    }

    public String getState() {
        return StringUtils.isNotBlank(state) ?
                state.equals(CloudPlatformEnum.UP.getState()) ?
                        CloudPlatformEnum.UP.toValue() : CloudPlatformEnum.DOWN.toValue()
                : alive ? CloudPlatformEnum.UP.toValue() : CloudPlatformEnum.DOWN.toValue();
    }

    public FontColorEnum getFontColor() {
        return getState().equals(CloudPlatformEnum.UP.getDesc()) ? FontColorEnum.GREEN : FontColorEnum.RED;
    }
}
