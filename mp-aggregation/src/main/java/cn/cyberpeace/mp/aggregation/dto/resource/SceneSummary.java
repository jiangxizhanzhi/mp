package cn.cyberpeace.mp.aggregation.dto.resource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/9/13 11:06
 */
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

    @ApiModelProperty("")
    private Integer competitiveDrillScene;

    @ApiModelProperty("")
    private Integer courseTraining;

    @ApiModelProperty("")
    private Integer skillsAssessment;

    @ApiModelProperty("")
    private Integer tacticalAssessment;

    @ApiModelProperty("")
    private Integer comprehensiveAssessment;

    @ApiModelProperty("")
    private Integer tacticalTraining;

    @ApiModelProperty("")
    private Integer comprehensiveTraining;

}
