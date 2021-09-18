package cn.cyberpeace.mp.devops.cluster.model;

import lombok.*;

import java.io.Serializable;

/**
 * 虚拟机模型，对应云平台接口返回信息
 */
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
    public static final class Reboot{

        private String rebootType;
    }
}
