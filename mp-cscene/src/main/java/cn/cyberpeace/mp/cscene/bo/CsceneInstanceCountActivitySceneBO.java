package cn.cyberpeace.mp.cscene.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CsceneInstanceCountActivitySceneBO {

    
    private Integer total;

    
    private Integer activitySceneTotal;
}
