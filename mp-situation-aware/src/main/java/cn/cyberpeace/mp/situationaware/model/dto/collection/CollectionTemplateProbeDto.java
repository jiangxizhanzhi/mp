package cn.cyberpeace.mp.situationaware.model.dto.collection;

import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class CollectionTemplateProbeDto {

    
    @ApiModelProperty(value = "探针ID")
    private String id;

    @ApiModelProperty(value = "采集模板id")
    private String templateId;

    
    @ApiModelProperty(value = "探针名称")
    private String name;

    
    @ApiModelProperty(value = "描述")
    private String description;

    
    @ApiModelProperty(value = "0:信息类 1:事件类 2:日志类")
    private Integer probeCategory;

    
    @ApiModelProperty(value = "0:通用探针 1:自定义探针")
    @DefaultInt(1)
    private Integer type;

    
    @ApiModelProperty(value = "创建者用户名")
    private String creator;

    
    @ApiModelProperty(value = "探针执行间隔，为大于1的数值，单位秒")
    private Integer intervalSec;

    
    @ApiModelProperty(value = "自定义探针输出格式: none, json, plain")
    private String outputFormat;

    
    @ApiModelProperty(value = "分析引擎")
    private String engine;

    
    @ApiModelProperty(value = "1:删除 0:未删除")
    private Integer isDeleted;

    
    @ApiModelProperty(value = "(可多选)操作系统类型 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other")
    private String osType;

    
    @ApiModelProperty(value = "0:启用，1:禁用")
    private Integer disabled;

    
    @ApiModelProperty(value = "0:shell 1:python 2:bat")
    private Integer scriptType;

    
    @ApiModelProperty(value = "探针脚本内容fdfs地址(采集模板单独存一份，防止自定义探针代码修改之后，影响到采集模板)")
    private String scriptUrl;

    
    @ApiModelProperty(value = "探针脚本内容，只有查询模板下单个自定义探针的接口才会加载这个字段")
    private String scriptContent;

    
    @ApiModelProperty(value = "探针执行间隔，为大于1的数值，单位秒，采集模板该值可以覆盖探针的interval_sec")
    private Integer templateProbeIntervalSec;

    
    @ApiModelProperty(value = "探针配置，json串，数组 {\"paths\": [\n\"/var/log/mp.log,/var/log/cr.log\",\n\"/etc/ossec\"]}")
    private String config;

    
    
    


    
    @ApiModelProperty(value = "脚本参数")
    private String parameter;

}
