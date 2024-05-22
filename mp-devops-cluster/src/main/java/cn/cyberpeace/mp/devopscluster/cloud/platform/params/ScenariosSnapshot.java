package cn.cyberpeace.mp.devopscluster.cloud.platform.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScenariosSnapshot {

    /**
     * 虚拟机/容器ID
     */
    private String instanceUuid;

    /**
     * 类型 vm/container
     */
    private String imageType;

    /**
     * 描述
     */
    private String description;
}
