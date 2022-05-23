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
    private String id;

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

    @ApiModelProperty(value = "1:true, 0:false  自定义探针是否执行导入操作，如果是导入，表示需要从自定义探针配置抓取一份最新的代码")
    private Integer reload;
}
