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
public class ColTempCommonProbeRequest {

    /**
     * 探针ID
     */
    @ApiModelProperty(value = "探针ID")
    @NotBlank
    private String probeId;

    /**
     * 0:启用，1:禁用
     */
    @ApiModelProperty(value = "0:启用，1:禁用")
    @NotNull
    private Integer disabled;

    /**
     * 探针执行间隔，为大于1的数值，单位秒，采集模板该值可以覆盖探针的interval_sec
     */
    //@ApiModelProperty(value = "探针执行间隔，为大于1的数值，单位秒，采集模板该值可以覆盖探针的interval_sec")
    //private Integer templateProbeIntervalSec;

    /**
     * 探针配置，json串，数组
     */
    @ApiModelProperty(value = "探针配置，路径数组 如：[\"/var/log/mp.log\", \"/etc/bin\"] 或者注册表选项 [\"HKEY_LOCAL_MACHINE\\SYSTEM\\Setup\"]")
    private List<String> paths;

}
