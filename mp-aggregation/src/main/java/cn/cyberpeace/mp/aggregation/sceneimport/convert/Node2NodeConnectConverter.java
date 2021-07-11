package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NodeElementParseResult;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortConnectionBO;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


class Node2NodeConnectConverter implements IConverter {

    private List<NodeElementParseResult> nodes;

    public Node2NodeConnectConverter(List<NodeElementParseResult> nodes) {
        this.nodes = nodes;
    }

    public void convert2BO(ConvertDataContainer container) {
        Set<String> networkIds = new HashSet<>();
        if (CollectionUtils.isNotEmpty(container.getNetworkElements())) {
            container.getNetworkElements().forEach(e -> networkIds.add(e.getId()));
        }
        var node2NodePort = container.getNode2NodePort();
        Set<String> connection = new HashSet<>();
        List<Node2Node> node2Nodes = new ArrayList<>();
        for (var node : nodes) {
            if (CollectionUtils.isEmpty(node.getNet())) {
                continue;
            }
            for (var net : node.getNet()) {
                
                if (networkIds.contains(net.getId())) {
                    continue;
                }
                String key = node.getId() + "_" + net.getId();
                if (!connection.contains(key)) {
                    connection.add(key);
                    connection.add(net.getId() + "_" + node.getId());
                    Node2Node node2Node = new Node2Node();
                    node2Node.setFrom(node.getId());
                    node2Node.setTo(net.getId());
                    node2Nodes.add(node2Node);
                }
            }
        }
        for (var node2Node : node2Nodes) {
            String from = node2Node.getFrom();
            String to = node2Node.getTo();
            var portFrom = node2NodePort.get(from + "_" + to);
            var portTo = node2NodePort.get(to + "_" + from);
            
            SceneConfigElementPortConnectionBO connectionBO = new SceneConfigElementPortConnectionBO();
            connectionBO.setId(TextUtils.uuid());
            connectionBO.setSource(portFrom.getId());
            connectionBO.setTarget(portTo.getId());
            
            if (container.getPortId2Protocol().get(portFrom.getId()) != null) {
                var vertices = container.getPortId2Protocol().get(portFrom.getId()).getVertices();
                if (StringUtils.isNotEmpty(vertices)) {
                    var verticesList = JsonCommon.parse(vertices, new TypeReference<List<Map<String, Object>>>() {
                    });
                    connectionBO.setVertices(verticesList);
                } else {
                    connectionBO.setVertices(new ArrayList<>());
                }
                connectionBO.setDisplayContext(container.getPortId2Protocol().get(portFrom.getId()).getDisplayContext());
            }
            connectionBO.setName(portFrom.getName() + "_" + portTo.getName());
            container.addConnection(connectionBO);
        }
    }

    @Getter
    @Setter
    private static class Node2Node {
        private String from;
        private String to;
    }
}
