package cn.cyberpeace.mp.collectionapi.init;

import cn.cyberpeace.mp.collection.config.ClusterConfig;
import cn.cyberpeace.mp.collectionapi.config.GroupConfig;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;

@Component
public class CollectionApiInit implements ApplicationRunner {

    @Resource
    private GroupConfig groupConfig;

    @Override
    public void run(ApplicationArguments args) {
        init(groupConfig);
    }

    public static void init(GroupConfig groupConfig) {
        // 初始化es及wazuh节点
        if (CollectionUtils.isNotEmpty(groupConfig.getGroups())) {
            ClusterConfig.nodes = new ArrayList<>();
            groupConfig.getGroups().forEach(group -> {
                ClusterConfig.Node node = new ClusterConfig.Node();
                node.setEsAddress(group.getEsNode());
                node.setNodeId(group.getGroupId());
                node.setWazuhUrl(group.getWazuhUrl());
                ClusterConfig.nodes.add(node);
            });
        }
    }
}
