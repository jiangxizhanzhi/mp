package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import lombok.Data;

import java.io.Serializable;

/**
 * mp_situation_collection_template_probe
 * @author 
 */
@Data
public class CollectionTemplateProbeModel extends BaseModel implements Serializable {

    /**
     * 采集模板id
     */
    private String templateId;

    /**
     * 探针id
     */
    private String probeId;

    /**
     * 0:不启用，1:启用
     */
    private Integer disabled;

    /**
     * 探针执行间隔，为大于1的数值，单位秒
     */
    private Integer intervalSec;

    /**
     * 探针配置，json串，数组
     */
    private String config;

}
