package cn.cyberpeace.mp.devopscluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContainerModel implements Serializable {

    
    private String id;

    
    private String name;

    
    private String description;

    
    private String image;

    
    private String imageDriver;

    
    private String host;

    
    private HashMap<String, Object> addresses;

    
    private List<String> volumes;

    
    private String hostname;

    
    private Integer cpu;

    
    private Integer ram;

    
    private Integer disk;

    
    private Boolean interactive;

    
    private Object command;

    
    private String workdir;

    
    private String status;


    private String runtime;

    
    private Object environment;

    
    private Object labels;

    
    private List<String> ports;

    
    private Boolean privileged;

    
    private String user;

    
    private String project;

    
    private String createdAt;
    
    private String updatedAt;

    
    private Long runningTime;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Address{

        private String addr;

        private Integer version;

        private String port;

        private String subnetId;

        private Boolean preserveOnDelete;

        private Integer vlanId;

    }
}
