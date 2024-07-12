 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name IConverter.java.
 // File size 910.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;

/**
 * @ClassName IConverter
 * @Description
 * @Author maojun
 * @Date 2021/10/12
 * @Version 1.0.0
 */
interface IConverter {
    /**
     * convert2BO
     *
     * @param container
     */
    void convert2BO(ConvertDataContainer container);

    /**
     * getPortId
     *
     * @param nodeId
     * @param type
     * @param existPortNum
     * @return
     */
    static String getPortId(String nodeId, Integer type, int existPortNum) {
        if (type != null && type == BaseSceneElementModel.Type.TERMINAL) {
            return "port_" + nodeId + "_" + (existPortNum + 1);
        } else {
            return "port_" + nodeId + "_electrical_" + (existPortNum + 1);
        }
    }
}
