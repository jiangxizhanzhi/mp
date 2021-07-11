package cn.cyberpeace.mp.devopscluster.converter.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VlanIpStockBO {

    
    private Integer totalVlans;

    
    private Integer usedVlans;

    
    private Integer totalIps;

    
    private Integer usedIps;
}
