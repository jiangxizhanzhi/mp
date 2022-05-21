package cn.cyberpeace.mp.situationaware.model.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/25 09:41
 */
@Data
public class ColTempCustomProbeRequest {

    /**
     * 探针ID
     */
    @ApiModelProperty(value = "探针ID")
    @NotBlank
    private String probeId;

    /**
     * 探针执行间隔，为大于1的数值，单位秒，采集模板该值可以覆盖探针的interval_sec
     */
    //@ApiModelProperty(value = "探针执行间隔，为大于1的数值，单位秒，采集模板该值可以覆盖探针的interval_sec")
    //private Integer templateProbeIntervalSec;

    /**
     * 脚本参数(JSON)
     */
    @ApiModelProperty(value = "脚本参数")
    private String parameter;
}
