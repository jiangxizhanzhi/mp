 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ImageParseResult.java.
 // File size 372.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @ClassName ImageParseResult
 * @Description
 * @Author maojun
 * @Date 2021/11/2
 * @Version 1.0.0
 */
@Setter
@Getter
public class ImageParseResult {
    private Map<String, ImageInfoParseResult> servers;
}
