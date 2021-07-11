package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("mp_situation_collection_template_probe")
public class CollectionTemplateProbeModel extends BaseModel implements Serializable {

    
    @ApiModelProperty(value = "采集模板id")
    private String templateId;

    
    @ApiModelProperty(value = "探针id")
    private String probeId;

    
    @ApiModelProperty(value = "探针名称")
    @DefaultStr
    private String name;

    
    @ApiModelProperty(value = "描述")
    @DefaultStr
    private String description;

    
    @ApiModelProperty(value = "0:信息类 1:事件类 2:日志类")
    @DefaultInt(0)
    private Integer probeCategory;

    
    @ApiModelProperty(value = "0:通用探针 1:自定义探针")
    @DefaultInt(1)
    private Integer type;


    
    @ApiModelProperty(value = "自定义探针输出格式: none, json, plain")
    @DefaultStr("json")
    private String outputFormat;

    
    @ApiModelProperty(value = "分析引擎")
    @DefaultStr("")
    private String engine;


    
    @ApiModelProperty(value = "(可多选)操作系统类型 0:Windows, 1:Unix, 2:Linux, 3:BSD, 4:Other")
    @DefaultStr
    private String osType;

    
    @ApiModelProperty(value = "创建者用户名")
    @DefaultStr
    private String creator;

    
    @ApiModelProperty(value = "0:启用，1:禁用")
    @DefaultInt(0)
    private Integer disabled;

    
    @ApiModelProperty(value = "0:shell 1:python 2:bat")
    @DefaultInt(0)
    private Integer scriptType;
    
    @ApiModelProperty(value = "探针脚本内容fdfs地址(采集模板单独存一份，防止自定义探针代码修改之后，影响到采集模板)")
    @DefaultStr
    private String scriptUrl;

    
    
    
    

    
    @ApiModelProperty(value = "探针执行间隔，为大于1的数值，单位秒，采集模板该值可以覆盖探针的interval_sec")
    @DefaultInt(-1)
    private Integer intervalSec;


    
    @ApiModelProperty(value = "探针配置，json串，数组")
    @DefaultStr("")
    private String config;

    
    @ApiModelProperty(value = "脚本参数")
    @DefaultStr
    private String parameter;

}
