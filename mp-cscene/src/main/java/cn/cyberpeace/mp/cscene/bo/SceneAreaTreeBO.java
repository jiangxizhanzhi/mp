 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneAreaTreeBO.java.
 // File size 1000.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName SceneAreaTreeBO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class SceneAreaTreeBO {
    @ApiModelProperty(value = "区域列表")
    private List<Area> areaList = new ArrayList<>();

    @Data
    public static class Area {
        @ApiModelProperty(value = "节点id")
        private String id;
        @ApiModelProperty(value = "父区域节点id")
        private String label;
        @ApiModelProperty(value = "名称")
        private String name;
        @ApiModelProperty(value = "描述")
        private String description;
        @ApiModelProperty(value = "显示参数")
        private String displayContext;
        @ApiModelProperty(value = "子区域列表")
        private List<Area> children = new ArrayList<>();
    }
}
