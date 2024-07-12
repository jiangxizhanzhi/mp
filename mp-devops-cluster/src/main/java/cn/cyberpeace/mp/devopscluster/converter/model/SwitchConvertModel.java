 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SwitchConvertModel.java.
 // File size 838.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName SwitchConvertModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SwitchConvertModel {

    @ApiModelProperty(value = "标签名")
    private String label;

    @ApiModelProperty(value = "标签值")
    private String value;

    @ApiModelProperty(value = "字标签")
    private List<Children> children;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Children{

        private String label;

        private String value;
    }
}
