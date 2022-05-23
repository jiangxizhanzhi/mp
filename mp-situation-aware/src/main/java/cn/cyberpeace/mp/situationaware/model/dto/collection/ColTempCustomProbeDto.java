package cn.cyberpeace.mp.situationaware.model.dto.collection;

import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 通用探针Dto
 * 参数比较少
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/25 09:41
 */
@Data
public class ColTempCustomProbeDto extends ColTempCommonProbeDto {

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 0:信息类 1:事件类 2:日志类
     */
    @ApiModelProperty(value = "类型 0:信息类 1:事件类 2:日志类")
    private Integer probeCategory;

    /**
     * (可多选)操作系统类型 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other
     */
    @ApiModelProperty(value = "(可多选)操作系统类型 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other")
    private String osType;

    /**
     * 自定义探针脚本参数(JSON)
     */
    @ApiModelProperty(value = "脚本参数")
    private String parameter;

    /**
     * 自定义探针不需要这个paths
     */
    @JsonIgnore
    private List<String> paths;

    /**
     * 探针执行间隔，为大于1的数值，单位秒，采集模板该值可以覆盖探针的interval_sec
     */
    //@ApiModelProperty(value = "探针执行间隔，为大于1的数值，单位秒，采集模板该值可以覆盖探针的interval_sec")
    //private Integer templateProbeIntervalSec;


}
