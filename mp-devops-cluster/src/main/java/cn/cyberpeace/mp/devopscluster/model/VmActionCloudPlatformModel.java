package cn.cyberpeace.mp.devopscluster.model;

import lombok.*;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class VmActionCloudPlatformModel extends BaseModel implements Serializable {

    private Reboot reboot;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Reboot implements Serializable{

        private String rebootType;
    }
}
