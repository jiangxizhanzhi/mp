package cn.cyberpeace.mp.devops.cluster.model;

import lombok.*;

import java.io.Serializable;

/**
 * 容器创建模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ContainerActionCloudPlatformModel extends BaseModel implements Serializable {

    /**
     * 操作
     */
    private String action;

    /**
     * 状态
     */
    private Boolean waitForStatus;
}
