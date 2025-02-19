package cn.cyberpeace.mp.cscene.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GuacamoleRep {

    private String id ;
    private String accessUser ;
    private String accessIp ;
    private String vmName;
    private String nodeId;
}
