package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * mp_situation_collection_template_probe
 * @author 
 */
@Data
public class CollectionTemplateProbeModel extends BaseModel implements Serializable {

    /**
     * 采集模板id
     */
    @ApiModelProperty(value = "采集模板id")
    private String templateId;

    /**
     * 探针id
     */
    @ApiModelProperty(value = "探针id")
    private String probeId;

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
    @ApiModelProperty(value = "探针配置，json串，数组")
    private Map<String, String> config;

}
