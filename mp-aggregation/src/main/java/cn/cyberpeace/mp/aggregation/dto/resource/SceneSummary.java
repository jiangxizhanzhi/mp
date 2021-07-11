package cn.cyberpeace.mp.aggregation.dto.resource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@ApiModel("资源统计-场景统计")
public class SceneSummary {

    @ApiModelProperty("课程训练")
    private Integer courseTrainingScene;

    @ApiModelProperty("强化训练")
    private Integer comprehensiveTrainingScene;

    @ApiModelProperty("红蓝对抗")
    private Integer redvsblueScene;

    @ApiModelProperty("靶场测试")
    private Integer cyberTestScene;

    @ApiModelProperty("竞技演练")
    private Integer competitiveDrillScene;

    @ApiModelProperty("课程训练")
    private Integer courseTraining;

    @ApiModelProperty("技能评估")
    private Integer skillsAssessment;

    @ApiModelProperty("战术评估")
    private Integer tacticalAssessment;

    @ApiModelProperty("综合评估")
    private Integer comprehensiveAssessment;

    @ApiModelProperty("战术训练")
    private Integer tacticalTraining;

    @ApiModelProperty("综合训练")
    private Integer comprehensiveTraining;

}
