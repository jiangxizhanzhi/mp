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
public class InstanceBO {

    
    private String id;

    
    private String name;

    
    private String node;

    
    private List<String> addressList;

    
    private String status;

    
    private String createTime;

    
    private String userName;

    
    private Long runningTime;

}
