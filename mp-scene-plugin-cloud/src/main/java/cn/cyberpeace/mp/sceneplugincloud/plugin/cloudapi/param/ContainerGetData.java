package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.catalina.startup.SetAllPropertiesRule;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class ContainerGetData implements CloudHttpData {
    private String id;
    private String name;
    private String description;
    private String image;
    private String imageDriver;
    private String host;
    private Map<String, List<Address>> addresses;
    private String volumes;
    private String hostname;
    private Integer cpu;
    private String ram;
    private Integer disk;
    private Boolean interactive;
    private List<String> command;
    private String workdir;
    private String status;
    private String runtime;
    private Object environment;
    private Object labels;
    private List<String> ports;
    private Boolean privileged;
    private String user;
    private String project;
    private String updatedAt;
    private String createdAt;


    @Data
    public static class Address{
        private String addr;
        private Integer version;
        private String port;
        private String subnetId;
        private Boolean preserveOnDelete;
    }
}
