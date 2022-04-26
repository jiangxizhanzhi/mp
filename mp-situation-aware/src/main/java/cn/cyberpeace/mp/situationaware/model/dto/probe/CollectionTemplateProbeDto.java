package cn.cyberpeace.mp.situationaware.model.dto.probe;

import cn.cyberpeace.mp.situationaware.model.ProbeModel;
import cn.cyberpeace.mp.situationaware.model.ProbeParamModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/25 09:41
 */
@Data
public class CollectionTemplateProbeDto extends ProbeModel {

    /**
     * 0:启用，1:禁用
     */
    @ApiModelProperty(value = "0:启用，1:禁用")
    private Integer disabled;

    /**
     * 探针执行间隔，为大于1的数值，单位秒，采集模板该值可以覆盖探针的interval_sec
     */
    @ApiModelProperty(value = "探针执行间隔，为大于1的数值，单位秒，采集模板该值可以覆盖探针的interval_sec")
    private Integer intervalSec;

    /**
     * 探针配置，json串，数组
     */
    @ApiModelProperty(value = "探针配置，json串，数组 {\"paths\": [\n\"/var/log/mp.log,/var/log/cr.log\",\n\"/etc/ossec\"]}")
    private Map<String, String> config;

    /**
     * 参数列表
     */
    @ApiModelProperty(value = "参数列表")
    private List<ProbeParamModel> paramList;

}
