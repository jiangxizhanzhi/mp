package cn.cyberpeace.mp.situationaware.bo;

import cn.cyberpeace.mp.situationaware.bo.common.AttackDestination;
import cn.cyberpeace.mp.situationaware.bo.common.AttackNetwork;
import cn.cyberpeace.mp.situationaware.bo.common.AttackSource;
import cn.cyberpeace.mp.situationaware.bo.common.AttackSuricata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowLogBO {

    
    private AttackSource source;

    
    private AttackDestination destination;

    
    private AttackNetwork network;

    
    private AttackSuricata suricata;

    
    private Long timestamp;

    
    private List<String> protocol;

}
