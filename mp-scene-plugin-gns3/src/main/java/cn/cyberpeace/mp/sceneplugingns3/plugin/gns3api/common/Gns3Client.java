package cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.common;

import cn.cyberpeace.mp.sceneplugingns3.constant.Gns3UrlConstant;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.CreateCloudResult;
import cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param.base.BaseNodeCreateData;
import cn.cyberpeace.mp.sceneplugingns3.util.gns3http.Gns3HttpClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public abstract class Gns3Client {

    private final Gns3HttpClient httpClient = new Gns3HttpClient();

    public String create(String projectId, BaseNodeCreateData baseNodeCreateData) {
        String url = Gns3UrlConstant.CREATE_PROJECT_NODE.replace("{project_id}", projectId);
        var result = getHttpClient().post(url, baseNodeCreateData, null, CreateCloudResult.class);
        return result.getNodeId();
    }

    public void start(String projectId, String nodeId) {
        String url = Gns3UrlConstant.START_PROJECT_NODE.replace("{project_id}", projectId);
        url = url.replace("{node_id}", nodeId);
        try {
            getHttpClient().post(url, null, null, Object.class);
        } catch (Exception e) {
            if (!"Entity may not be null".equals(e.getMessage())) {
                throw e;
            }
        }
    }

    public void delete(String projectId, String nodeId) {
        String url = Gns3UrlConstant.CREATE_PROJECT_NODE.replace("{project_id}", projectId);
        url = url.replace("{node_id}", nodeId);
        try {
            getHttpClient().delete(url, null, null, Object.class);
        } catch (Exception e) {
            if (!"Entity may not be null".equals(e.getMessage())) {
                throw e;
            }
        }
    }
}
