 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowAttackConfigInfo.java.
 // File size 710.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.dto.flow;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName FlowAttackConfigInfo
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/12/1 19:04
 * @Version 1.0.0
 */
@Data
public class FlowAttackConfigInfo {

    @ApiModelProperty("攻击类型列表")
    private List<FlowAttackType> types;

    @Data
    public static class FlowAttackType {
        @ApiModelProperty("攻击类型名称")
        private String name;

        @ApiModelProperty("攻击类型")
        private String value;

        @ApiModelProperty("攻击等级")
        private Integer severity;
    }

}
