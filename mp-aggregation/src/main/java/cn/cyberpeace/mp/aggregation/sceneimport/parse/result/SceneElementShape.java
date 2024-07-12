 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneElementShape.java.
 // File size 519.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.sceneimport.parse.result;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @ClassName SceneElementShape
 * @Description
 * @Author maojun
 * @Date 2022/8/29
 * @Version 1.0.0
 */
@Getter
@Setter
public class SceneElementShape {
    private String id;
    private String name;
    private String position;
    private Double length;
    private Double width;
    private List<String> nodeIds;
    private String displayContext;
}
