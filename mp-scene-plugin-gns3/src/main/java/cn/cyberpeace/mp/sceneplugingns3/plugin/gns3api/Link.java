package cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api;

import cn.cyberpeace.mp.sceneplugingns3.constant.Gns3UrlConstant;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.common.Gns3Client;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.LinkNodeParams;

import java.util.ArrayList;
import java.util.List;


public class Link extends Gns3Client {
    public void link(String projectId, String fromNode, Integer fromPort, String toNode, Integer toPort) {
        LinkNodeParams.LinkNode from = new LinkNodeParams.LinkNode();
        from.setNodeId(fromNode);
        from.setPortNumber(fromPort);

        LinkNodeParams.LinkNode to = new LinkNodeParams.LinkNode();
        to.setNodeId(toNode);
        to.setPortNumber(toPort);

        List<LinkNodeParams.LinkNode> list = new ArrayList<>();
        list.add(from);
        list.add(to);

        LinkNodeParams params = new LinkNodeParams();
        params.setNodes(list);
        String url = Gns3UrlConstant.CREATE_PROJECT_LINK.replace("{project_id}", projectId);
        getHttpClient().post(url, params, null, Object.class);
    }
}
