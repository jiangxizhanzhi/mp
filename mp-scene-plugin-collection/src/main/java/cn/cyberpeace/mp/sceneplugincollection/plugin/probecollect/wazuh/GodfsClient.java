package cn.cyberpeace.mp.sceneplugincollection.plugin.probecollect.wazuh;

import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpClient;
import cn.cyberpeace.mp.sceneplugincloud.util.http.HttpParam;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GodfsClient {

    @Getter
    private HttpClient godfsHttpClient;

    public GodfsClient(String godfsServer) {
        this.godfsHttpClient = new HttpClient(godfsServer, 600000, 200, 200);
        this.godfsHttpClient.setLogRequest(true);
    }

    public boolean deleteGodfsFile(String path) {
        var deleteParam = new GodfsDeleteParam();
        deleteParam.setPath(path);
        var httpResult = godfsHttpClient.post("/group1/delete", null, deleteParam, null, null);
        if (!httpResult.isSuccess()) {
            log.error("delete godfs file {} failed: {}", path, httpResult.getResult());
            return false;
        }

        log.info("delete godfs file {} response: {}", path, httpResult.getResult());
        return true;
    }

    @Data
    public static class GodfsDeleteParam implements HttpParam {
        private String path;
        private String md5;
    }

}
