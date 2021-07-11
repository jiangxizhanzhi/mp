package cn.cyberpeace.mp.situationaware.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionSourceModel {

    
    private String index;

    private List<String> protocol;

    
    private SessionSource source;

    
    private SessionDestination destination;

    
    private SessionNetwork network;

    
    private SessionSuricata suricata;

    
    private Long timestamp;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class SessionSource{

        
        private String ip;

        
        private Integer port;

        
        private List<String> mac;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class SessionDestination{

        
        private String ip;

        
        private Integer port;

        
        private List<String> mac;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class SessionNetwork{

        private Long bytes;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class SessionSuricata{

        private List<String> category;

    }
}
