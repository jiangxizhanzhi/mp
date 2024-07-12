 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneInfoVO.java.
 // File size 657.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName SceneInfoVO
 * @Description
 * @Author zhouxinghuai
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseVO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SceneInfoVO extends BaseVO {

    /**
     * 场景名称
     */
    @ApiModelProperty("场景名称")
    private String sceneName;


    /**
     * 场景类型
     */
    @ApiModelProperty(value = "引用类型")
    private Integer referenceType;


}
