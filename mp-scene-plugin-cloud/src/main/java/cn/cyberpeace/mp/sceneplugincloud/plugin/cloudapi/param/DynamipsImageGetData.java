package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.base.util.http.HttpData;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DynamipsImageGetData implements HttpData {
    // 镜像ID
    private String id;
    // 镜像名
    private String name;
    // 镜像类型
    private String type;
    // 镜像文件
    @JsonProperty(value = "fileName")
    private String fileName;
    private String path;
    // 平台
    private String platform;
}