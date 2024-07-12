 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name PrepareConverter.java.
 // File size 1869.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NetworkElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NodeElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.YmlParseResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @ClassName PrepareConverter
 * @Description 将解析出的scene节点转成BO
 * @Author maojun
 * @Date 2021/10/12
 * @Version 1.0.0
 * @see IConverter
 */
class PrepareConverter implements IConverter {

    private YmlParseResult parseResult;

    /**
     * PrepareConverter
     *
     * @param parseResult
     */
    public PrepareConverter(YmlParseResult parseResult) {
        this.parseResult = parseResult;
    }

    /**
     * convert2BO
     *
     * @param container
     */
    public void convert2BO(ConvertDataContainer container) {
        if (CollectionUtils.isNotEmpty(parseResult.getNetworks()) && CollectionUtils.isNotEmpty(parseResult.getNodes())) {
            Set<String> simpleSwitchIds = new HashSet<>();
            for (NetworkElementParseResult n : parseResult.getNetworks()) {
                simpleSwitchIds.add("router-" + n.getId());
            }
            Iterator<NodeElementParseResult> it = parseResult.getNodes().iterator();
            while (it.hasNext()) {
                NodeElementParseResult node = it.next();
                if (simpleSwitchIds.contains(node.getId())) {
                    container.getSimpleLayer3Switch().add(StringUtils.replaceOnce(node.getId(), "router-", ""));
                    it.remove();
                }
            }
        }
    }

}
