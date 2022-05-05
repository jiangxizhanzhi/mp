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

/**
 * 探针 实体
 * mp_situation_probe
 * @author 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("mp_situation_probe")
public class ProbeModel extends BaseModel {

    public static final int TYPE_COMMON = 0;

    public static final int TYPE_CUSTOM = 1;

    public static final int CATATORY_BASIC_INFO = 0;

    public static final int CATATORY_EVENT = 1;

    public static final int CATATORY_LOG = 2;

    private static final long serialVersionUID = 1L;

    /**
     * 探针名称
     */
    @ApiModelProperty(value = "探针名称")
    private String name;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 0:信息类 1:事件类 2:日志类 
     */
    @ApiModelProperty(value = "0:信息类 1:事件类 2:日志类")
    private Integer probeCategory;

    /**
     * 0:通用探针 1:自定义探针
     */
    @ApiModelProperty(value = "0:通用探针 1:自定义探针")
    @DefaultInt(1)
    private Integer type;

    /**
     * 创建者用户名
     */
    @ApiModelProperty(value = "创建者用户名")
    @DefaultStr
    private String creator;
    
    /**
     * 探针执行间隔，为大于1的数值，单位秒，默认60秒
     */
    @ApiModelProperty(value = "探针执行间隔，为大于1的数值，单位秒")
    @DefaultInt(60)
    private Integer intervalSec;

    /**
     * 自定义探针输出格式: none, json, plain
     */
    @ApiModelProperty(value = "自定义探针输出格式: none, json, plain")
    @DefaultStr("none")
    private String outputFormat;

    /**
     * 分析引擎
     */
    @ApiModelProperty(value = "分析引擎")
    @DefaultStr("")
    private String engine;

    /**
     * 1:删除 0:未删除
     */
    @ApiModelProperty(value = "1:删除 0:未删除")
    @DefaultInt(0)
    private Integer isDeleted;

    /**
     * (可多选)操作系统类型 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other
     */
    @ApiModelProperty(value = "(可多选)操作系统类型 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other")
    private String osType;

}
