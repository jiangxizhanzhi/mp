package cn.cyberpeace.mp.devopscluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class VlanIpStockModel implements Serializable {

    
    private Integer totalVlans;

    
    private Integer usedVlans;

    
    private Integer totalIps;

    
    private Integer usedIps;


}
