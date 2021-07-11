package cn.cyberpeace.mp.devopscluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstanceModel implements Serializable {

    
    private String id;

    
    private String uuid;

    
    private String name;

    
    private String description;

    
    private String image;

    
    private String flavor;

    
    private List<Address> addresses;

    
    private List<String> volumes;

    
    private String userData;

    
    private Object metadata;

    
    private String status;

    
    private String host;


    private String hostname;

    
    private String instanceName;

    
    private String user;

    
    private String project;

    
    private String createdAt;
    
    private String updatedAt;

    
    private Long runningTime;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Address implements Serializable{

        
        private String ip;

        
        private String mac;
    }
}
