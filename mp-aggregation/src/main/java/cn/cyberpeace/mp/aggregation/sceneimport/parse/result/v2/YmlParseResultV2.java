 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name YmlParseResultV2.java.
 // File size 1395.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.sceneimport.parse.result.v2;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NodeElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.SceneElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.SceneElementShape;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.YmlParseResult;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName YmlParseResultV2
 * @Description
 * @Author maojun
 * @Date 2021/10/9
 * @Version 1.0.0
 */
@Getter
@Setter
public class YmlParseResultV2 {
    private SceneElementParseResult scene;
    private List<NetworkElementParseResultV2> networks;
    private List<NodeElementParseResult> nodes;
    private List<SceneElementShape> shape;

    public YmlParseResult convert() {
        YmlParseResult result = new YmlParseResult();
        result.setScene(scene);
        result.setNodes(nodes);
        result.setShape(shape);
        result.setNetworks(new ArrayList<>());
        if (CollectionUtils.isNotEmpty(networks)) {
            for (NetworkElementParseResultV2 n : networks) {
                result.getNetworks().add(n.convert());
            }
        }
        return result;
    }
}