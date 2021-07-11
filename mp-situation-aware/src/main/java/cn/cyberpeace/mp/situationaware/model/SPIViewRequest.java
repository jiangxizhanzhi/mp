package cn.cyberpeace.mp.situationaware.model;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SPIViewRequest {

    
    private Long startTime;

    
    private Long stopTime;

    
    @NotNull
    private String groupId;
}
