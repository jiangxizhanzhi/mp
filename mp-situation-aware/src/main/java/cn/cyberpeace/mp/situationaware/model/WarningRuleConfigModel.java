package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @ClassName WarningRuleConfigModel
 * @Description 告警规则配置，配置这个告警规则后，用采集到的数据匹配到对应的规则
 * @Author zhanzhi
 * @Date 2023/9/21 14:27
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table("mp_host_collect_map_warning_config")
public class WarningRuleConfigModel extends BaseModel {

    @ApiModelProperty("告警名称")
    private String warningName;

    @ApiModelProperty("告警方向 目前有0到4五个方向，具体看BO")
    private Integer warningTarget;

    @ApiModelProperty("告警等级，0-低危 1-中危 2-高危 ")
    private Integer warningClass;

    @ApiModelProperty("告警动作，可能会有多个，0-新增，1-变更，2-删除")
    private String warningMovements;

    @ApiModelProperty("系统类型，0-windows 1-linux")
    private Integer systemType;

    @ApiModelProperty("进程名称")
    private String processNames;

    @ApiModelProperty("进程详细描述匹配字段")
    private String processDetailFeatures;

    @ApiModelProperty("进程状态")
    private String processStatusList;

    @ApiModelProperty("文件路径")
    private String filePaths;

    @ApiModelProperty("文件后缀")
    private String fileSuffixes;

    @ApiModelProperty("文件最小大小")
    private String fileSizeMin;

}
