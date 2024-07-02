package cn.cyberpeace.mp.devopscluster.model;

import lombok.*;

import java.io.Serializable;

/**
 * @ClassName VmActionCloudPlatformModel
 * @Description 虚拟机模型，对应云平台接口返回信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 * @see Serializable
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
    public static final class Reboot implements Serializable{

        private String rebootType;
    }
}
