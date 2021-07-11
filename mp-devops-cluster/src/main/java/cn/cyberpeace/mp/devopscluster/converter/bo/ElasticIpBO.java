package cn.cyberpeace.mp.devopscluster.converter.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElasticIpBO {

    
    private String fixedIpAddress;

    
    private String floatIpAddress;

    
    private String status;

    
    private String createdAt;
}
