package cn.cyberpeace.mp.devops.cluster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnapshotCreate {

    /**
     * UUID
     */
    private String deviceUuid;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;
}
