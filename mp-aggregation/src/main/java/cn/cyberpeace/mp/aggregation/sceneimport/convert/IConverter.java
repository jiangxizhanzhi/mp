package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/12
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
