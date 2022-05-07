package cn.cyberpeace.mp.situationaware.model.dto.collection;

import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
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
public class CollectionTemplateProbeDto {

    /**
     * 探针ID
     */
    @ApiModelProperty(value = "探针ID")
    private String probeId;

    @ApiModelProperty(value = "采集模板id")
    private String templateId;

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
    private Integer creator;

    /**
     * 探针执行间隔，为大于1的数值，单位秒，默认60秒
     */
    @ApiModelProperty(value = "探针执行间隔，为大于1的数值，单位秒")
    private Integer intervalSec;

    /**
     * 自定义探针输出格式: none, json, plain
     */
    @ApiModelProperty(value = "自定义探针输出格式: none, json, plain")
    private String outputFormat;

    /**
     * 分析引擎
     */
    @ApiModelProperty(value = "分析引擎")
    private String engine;

    /**
     * 1:删除 0:未删除
     */
    @ApiModelProperty(value = "1:删除 0:未删除")
    private Integer isDeleted;

    /**
     * (可多选)操作系统类型 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other
     */
    @ApiModelProperty(value = "(可多选)操作系统类型 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other")
    private String osType;

    /**
     * 0:启用，1:禁用
     */
    @ApiModelProperty(value = "0:启用，1:禁用")
    private Integer disabled;

    /**
     * 探针脚本内容fdfs地址（采集模板单独存一份，防止自定义探针代码修改之后，影响到采集模板）
     */
    @ApiModelProperty(value = "探针脚本内容fdfs地址(采集模板单独存一份，防止自定义探针代码修改之后，影响到采集模板)")
    private String scriptUrl;

    /**
     * 探针执行间隔，为大于1的数值，单位秒，采集模板该值可以覆盖探针的interval_sec
     */
    @ApiModelProperty(value = "探针执行间隔，为大于1的数值，单位秒，采集模板该值可以覆盖探针的interval_sec")
    private Integer templateProbeIntervalSec;

    /**
     * 探针配置，json串，数组
     */
    @ApiModelProperty(value = "探针配置，json串，数组 {\"paths\": [\n\"/var/log/mp.log,/var/log/cr.log\",\n\"/etc/ossec\"]}")
    private String config;

    /**
     * 探针配置，json串，数组
     */
    @ApiModelProperty(value = "探针配置，json串，数组 {\"paths\": [\n\"/var/log/mp.log,/var/log/cr.log\",\n\"/etc/ossec\"]}")
    private Map<String, String> configMap;

    /**
     * 参数列表
     */
    @ApiModelProperty(value = "参数列表")
    private List<ProbeParamModel> paramList;

}
