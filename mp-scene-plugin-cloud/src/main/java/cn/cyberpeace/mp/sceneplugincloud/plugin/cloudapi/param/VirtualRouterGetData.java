package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class VirtualRouterGetData implements CloudHttpData {
    private String id;
    private String name;
    private String description;
    private Object externalGateway;
    private List<Object> routes;
    private List<Object> interfaces;
    private String project;
    private String status;
    private String updatedAt;
    private String createdAt;
}
