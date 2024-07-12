 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowBaseModel.java.
 // File size 656.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName FlowBaseModel
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowBaseModel {

    @ApiModelProperty("首次执行延迟时间，单位：秒(可以为0)")
    private int delay = 0;

    @ApiModelProperty("周期执行间隔，单位：秒")
    private int interval = 1;

    @ApiModelProperty("执行次数")
    private int count = 1;

}
