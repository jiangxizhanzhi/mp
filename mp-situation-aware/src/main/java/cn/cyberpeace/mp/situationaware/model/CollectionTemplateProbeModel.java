package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 采集模板-探针关系 实体
 * mp_situation_collection_template_probe
 * @author 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("mp_situation_collection_template_probe")
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
    @DefaultInt(0)
    private Integer disabled;

    /**
     * 探针执行间隔，为大于1的数值，单位秒，采集模板该值可以覆盖探针的interval_sec
     */
    @ApiModelProperty(value = "探针执行间隔，为大于1的数值，单位秒，采集模板该值可以覆盖探针的interval_sec")
    private Integer intervalSec;

    /**
     * 探针配置，json字符串
     */
    @ApiModelProperty(value = "探针配置，json串，数组")
    private String config;

}
