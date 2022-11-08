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
     * 探针名称
     */
    @ApiModelProperty(value = "探针名称")
    @DefaultStr
    private String name;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    @DefaultStr
    private String description;

    /**
     * 0:信息类 1:事件类 2:日志类
     */
    @ApiModelProperty(value = "0:信息类 1:事件类 2:日志类")
    @DefaultInt(0)
    private Integer probeCategory;

    /**
     * 0:通用探针 1:自定义探针
     */
    @ApiModelProperty(value = "0:通用探针 1:自定义探针")
    @DefaultInt(1)
    private Integer type;


    /**
     * 自定义探针输出格式: none, json, plain
     */
    @ApiModelProperty(value = "自定义探针输出格式: none, json, plain")
    @DefaultStr("json")
    private String outputFormat;

    /**
     * 分析引擎
     */
    @ApiModelProperty(value = "分析引擎")
    @DefaultStr("")
    private String engine;


    /**
     * (可多选)操作系统类型 0:Windows, 1:Unix, 2:Linux, 3:BSD, 4:Other
     */
    @ApiModelProperty(value = "(可多选)操作系统类型 0:Windows, 1:Unix, 2:Linux, 3:BSD, 4:Other")
    @DefaultStr
    private String osType;

    /**
     * 创建者用户名
     */
    @ApiModelProperty(value = "创建者用户名")
    @DefaultStr
    private String creator;

    /**
     * 0:启用，1:禁用
     */
    @ApiModelProperty(value = "0:启用，1:禁用")
    @DefaultInt(0)
    private Integer disabled;

    /**
     * 0:shell 1:python 2:bat
     */
    @ApiModelProperty(value = "0:shell 1:python 2:bat")
    @DefaultInt(0)
    private Integer scriptType;
    /**
     * 探针脚本内容fdfs地址（采集模板单独存一份，防止自定义探针代码修改之后，影响到采集模板）
     */
    @ApiModelProperty(value = "探针脚本内容fdfs地址(采集模板单独存一份，防止自定义探针代码修改之后，影响到采集模板)")
    @DefaultStr
    private String scriptUrl;

    /**
     * 探针脚本内容
     */
    //@ApiModelProperty(value = "探针脚本内容，只有查询模板下单个自定义探针的接口才会加载这个字段")
    //@DefaultStr
    //private String scriptContent;

    /**
     * 探针执行间隔，为大于1的数值，单位秒，采集模板该值可以覆盖探针的interval_sec
     */
    @ApiModelProperty(value = "探针执行间隔，为大于1的数值，单位秒，采集模板该值可以覆盖探针的interval_sec")
    @DefaultInt(-1)
    private Integer intervalSec;


    /**
     * 探针配置，json字符串
     */
    @ApiModelProperty(value = "探针配置，json串，数组")
    @DefaultStr("")
    private String config;

    /**
     * 脚本参数(JSON)
     */
    @ApiModelProperty(value = "脚本参数")
    @DefaultStr
    private String parameter;

}
