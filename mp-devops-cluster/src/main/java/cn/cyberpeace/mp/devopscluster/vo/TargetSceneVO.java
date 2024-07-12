 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TargetSceneVO.java.
 // File size 978.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName TargetSceneVO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetSceneVO implements Serializable {

    @ApiModelProperty("一级分类ID")
    private Integer firstTypeId;

    @ApiModelProperty("一级分类名称")
    private String firstTypeName;

    @ApiModelProperty("二级分类ID")
    private Integer secondTypeId;

    @ApiModelProperty("二级分类名称")
    private String secondTypeName;

    @ApiModelProperty("二级分类对应的靶标集合")
    private List<Device> deviceList;
}
