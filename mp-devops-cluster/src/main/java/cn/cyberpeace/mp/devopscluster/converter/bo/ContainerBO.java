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
public class ContainerBO {

    
    private String id;

    
    private String name;

    
    private String image;

    
    private String node;

    
    private List<String> addressList;

    
    private List<Integer> vlanIdList;

    
    private String status;

    
    private String createdAt;

    
    private String userName;

    
    private Long runningTime;
}
