package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NodeElementParseResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortConnectionBO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/12
 */
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
        Map<String, String> node2Nodes = new HashMap<>();
        for (var node : nodes) {
            if (CollectionUtils.isEmpty(node.getNet())) {
                continue;
            }
            for (var net : node.getNet()) {
                // 和网络的连接已由 NetworkElementConverter 处理
                if (networkIds.contains(net.getId())) {
                    continue;
                }
                String key = node.getId() + "_" + net.getId();
                if (!connection.contains(key)) {
                    connection.add(key);
                    connection.add(net.getId() + "_" + node.getId());
                    node2Nodes.put(node.getId(), net.getId());
                }
            }
        }
        for (Map.Entry<String, String> node2Node : node2Nodes.entrySet()) {
            String from = node2Node.getKey();
            String to = node2Node.getValue();
            var portFrom = node2NodePort.get(from + "_" + to);
            var portTo = node2NodePort.get(to + "_" + from);
            // 创建连线
            SceneConfigElementPortConnectionBO connectionBO = new SceneConfigElementPortConnectionBO();
            connectionBO.setId(TextUtils.uuid());
            connectionBO.setSource(portFrom.getId());
            connectionBO.setTarget(portTo.getId());
            connectionBO.setName(portFrom.getName() + "_" + portTo.getName());
            container.addConnection(connectionBO);
        }
    }
}
