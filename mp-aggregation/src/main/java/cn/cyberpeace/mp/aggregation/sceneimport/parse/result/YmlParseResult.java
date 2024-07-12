 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name YmlParseResult.java.
 // File size 497.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.sceneimport.parse.result;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @ClassName YmlParseResult
 * @Description
 * @Author maojun
 * @Date 2021/10/9
 * @Version 1.0.0
 */
@Getter
@Setter
public class YmlParseResult {
    private SceneElementParseResult scene;
    private List<NetworkElementParseResult> networks;
    private List<NodeElementParseResult> nodes;
    private List<SceneElementShape> shape;
}