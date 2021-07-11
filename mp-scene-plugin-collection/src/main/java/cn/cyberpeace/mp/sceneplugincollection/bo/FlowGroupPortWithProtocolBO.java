package cn.cyberpeace.mp.sceneplugincollection.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlowGroupPortWithProtocolBO {

   
   private String port;

    
   private String protocol;

    
    private Long attackNumber;

    
    private Double trafficSize;
}
