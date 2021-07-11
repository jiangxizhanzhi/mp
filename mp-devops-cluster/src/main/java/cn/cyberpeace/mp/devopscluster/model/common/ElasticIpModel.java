package cn.cyberpeace.mp.devopscluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElasticIpModel implements Serializable {

    
    private String id;

    
    private String tenantId;

    
    private String floatingIpAddress;

    
    private String floatingNetworkId;

    
    private String routerId;

    
    private String portId;

    
    private String fixedIpAddress;

    
    private String status;

    
    private String description;

    
    private String portDetails;

    
    private List<String> tags;

    
    private Integer revisionNumber;

    
    private String projectId;

    
    private String createdAt;
    
    private String updatedAt;
}
