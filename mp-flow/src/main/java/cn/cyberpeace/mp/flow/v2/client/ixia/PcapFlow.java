package cn.cyberpeace.mp.flow.v2.client.ixia;

import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/30
 **/
@Data
public class PcapFlow implements HttpData {
    private FileInfo fileInfo;
    private byte[] file;

    @Override
    public boolean multipart() {
        return true;
    }

    public String getFileInfo() {
        return JsonUtils.stringify(this.fileInfo);
    }

    @Getter
    @Setter
    public static class FileInfo {
        private String name;
    }

    @Getter
    @Setter
    public static class RecreateData {
        private String name;
        private String delay;
    }
}
