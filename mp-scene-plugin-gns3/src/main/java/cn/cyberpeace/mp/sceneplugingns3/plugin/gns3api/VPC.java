package cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api;

import cn.cyberpeace.mp.sceneplugingns3.constant.Gns3UrlConstant;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.common.Gns3Client;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.CreateCloudData;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.CreateCloudResult;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.UpdateVpcsData;

public class VPC extends Gns3Client {
    public String create(String projectId, String name) {
        CreateCloudData createCloudData = new CreateCloudData();
        createCloudData.setComputeId("local");
        createCloudData.setName(name);
        createCloudData.setNodeType("vpcs");
        createCloudData.setSymbol(":/symbols/vpcs_guest.svg");
        String url = Gns3UrlConstant.CREATE_PROJECT_NODE.replace("{project_id}", projectId);
        var result = getHttpClient().post(url, createCloudData, null, CreateCloudResult.class);
        return result.getNodeId();
    }

    public void update(String projectId, String nodeId, UpdateVpcsData updateVpcsData) {
        String url = Gns3UrlConstant.UPDATE_VPCS.replace("{project_id}", projectId);
        url = url.replace("{node_id}", nodeId);
        getHttpClient().post(url, updateVpcsData, null, Object.class);
    }

    public void delete(String projectId, String nodeId) {
        String url = Gns3UrlConstant.CREATE_PROJECT_NODE.replace("{project_id}", projectId);
        url = url.replace("{node_id}", nodeId);
        getHttpClient().delete(url, null, null, Object.class);
    }
}
