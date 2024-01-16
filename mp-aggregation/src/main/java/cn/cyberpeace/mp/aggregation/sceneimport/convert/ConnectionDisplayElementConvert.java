package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.ExtNodeInfo;
import cn.cyberpeace.mp.base.common.JsonCommon;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description 传递前端连线的属性
 * @Author maojun
 * @Date 2021/10/12
 */
class ConnectionDisplayElementConvert implements IConverter {

    public void convert2BO(ConvertDataContainer container) {

        if (CollectionUtils.isNotEmpty(container.getConnections()) && CollectionUtils.isNotEmpty(container.getPortId2Protocol().values())) {
            Map<String, ExtNodeInfo.PortProtocolExportInfo> portId2Protocol = container.getPortId2Protocol();
            container.getConnections().forEach(c -> {
                if (portId2Protocol.get(c.getSource()) != null) {
                    String vertices = portId2Protocol.get(c.getSource()).getVertices();
                    if (StringUtils.isNotEmpty(vertices)) {
                        List<Map<String, Object>> verticesList = JsonCommon.parse(vertices, new TypeReference<List<Map<String, Object>>>() {
                        });
                        c.setVertices(verticesList);
                    } else {
                        c.setVertices(new ArrayList<>());
                    }
                    c.setDisplayContext(portId2Protocol.get(c.getSource()).getDisplayContext());
                    c.setType(portId2Protocol.get(c.getSource()).getType());
                } else if (portId2Protocol.get(c.getTarget()) != null) {
                    String vertices = portId2Protocol.get(c.getTarget()).getVertices();
                    if (StringUtils.isNotEmpty(vertices)) {
                        List<Map<String, Object>> verticesList = JsonCommon.parse(vertices, new TypeReference<List<Map<String, Object>>>() {
                        });
                        c.setVertices(verticesList);
                    } else {
                        c.setVertices(new ArrayList<>());
                    }
                    c.setDisplayContext(portId2Protocol.get(c.getTarget()).getDisplayContext());
                    c.setType(portId2Protocol.get(c.getTarget()).getType());
                }
            });
        }
    }

}