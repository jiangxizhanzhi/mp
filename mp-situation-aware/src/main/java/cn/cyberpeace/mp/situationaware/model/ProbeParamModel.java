package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import lombok.Data;

import java.io.Serializable;

/**
 * mp_situation_probe_param
 * @author 
 */
@Data
public class ProbeParamModel extends BaseModel implements Serializable {

    /**
     * 探针id
     */
    private String probeId;

    /**
     * 采集模板id
     */
    private String templateId;

    /**
     * 参数名称
     */
    private String name;

    /**
     * 0:number 1:string 2:bool 3:ip
     */
    private Integer type;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 参数说明
     */
    private String description;


}
