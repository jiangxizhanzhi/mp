package cn.cyberpeace.mp.devopscluster.converter.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NetworkBO {

    
    private String name;

    
    private String networkType;

    
    private String status;

    
    private List<NetworkSubnetBO> subnets;

    
    private Long downloadSpeed;

    
    private Long uploadSpeed;

    
    private Integer nodeNum;

    
    private String createdAt;
}
