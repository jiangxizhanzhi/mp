package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import org.apache.commons.collections4.CollectionUtils;

/**
 * @Description 传递前端连线的属性
 * @Author maojun
 * @Date 2021/10/12
 */
class ConnectionDisplayElementConvert implements IConverter {

    public void convert2BO(ConvertDataContainer container) {

        if (CollectionUtils.isNotEmpty(container.getConnections()) && CollectionUtils.isNotEmpty(container.getPortId2Protocol().values())) {
            var portId2Protocol = container.getPortId2Protocol();
            container.getConnections().forEach(c -> {
                if (portId2Protocol.get(c.getSource()) != null) {
                    c.setDisplayContext(portId2Protocol.get(c.getSource()).getDisplayContext());
                    c.setType(portId2Protocol.get(c.getSource()).getType());
                } else if (portId2Protocol.get(c.getTarget()) != null) {
                    c.setDisplayContext(portId2Protocol.get(c.getTarget()).getDisplayContext());
                    c.setType(portId2Protocol.get(c.getTarget()).getType());
                }
            });
        }
    }

}