package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.RecreateTaskPool;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/25
 */
@Slf4j
public class ElementModifyHandler extends HandlerCommon {

    public ElementModifyHandler(CloudScenePluginHandler pluginHandler, int event) {
        super(pluginHandler, RecreateTaskPool.getInstance(), event);
    }


    public void modifyElement(CloudPluginSceneElementBO element) {
        var srcElement = element.getSrcElement();
        var originElement = this.getPluginHandler().getElement(element.getSrcElement().getId()).getSrcElement();
        boolean cpuChange = srcElement.getCpu() != originElement.getCpu();
        boolean diskChange = srcElement.getDisk() != originElement.getDisk();
        boolean ramChange = srcElement.getRam() != originElement.getRam();
        if (cpuChange || diskChange || ramChange) {
            // todo 调用底层接口更新节点
        }
    }

}
