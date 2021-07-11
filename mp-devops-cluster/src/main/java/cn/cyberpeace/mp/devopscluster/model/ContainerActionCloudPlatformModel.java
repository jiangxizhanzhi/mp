package cn.cyberpeace.mp.devopscluster.model;

import lombok.*;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ContainerActionCloudPlatformModel extends BaseModel implements Serializable {

    
    private String action;

    
    private Boolean waitForStatus;
}
